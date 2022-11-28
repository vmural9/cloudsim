package HelperUtils

import com.typesafe.config.{Config, ConfigBeanFactory, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.topologies.{BriteNetworkTopology, NetworkTopology}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import HelperUtils.CreateLogger
import org.cloudbus.cloudsim.core
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelDynamic}
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.autoscaling.{HorizontalVmScaling, HorizontalVmScalingSimple}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.brokers.DatacenterBroker
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.vms.VmCost
import org.slf4j.Logger

import java.util
import java.util.logging.Level
import scala.jdk.CollectionConverters.*
import scala.util.Random

object helperfunctions {
  val logger = CreateLogger(classOf[core.Simulation])


  def generateHost(conf: ConfigReader) = {
    val peList = (1 to conf.pesHost).map { _ =>new PeSimple(1000).asInstanceOf[Pe]}.toList
    val host = new NetworkHost(conf.ramHost, conf.bwHost, conf.sizeHost, peList.asJava)
    host.setVmScheduler(conf.schedPolicy match {
      case "SpaceShared" =>
        logger.info("Space Shared VM Scheduling is enabled.")
        new VmSchedulerSpaceShared()
      case "TimeShared" =>
        logger.info("Time Shared VM Scheduling is enabled.")
        new VmSchedulerTimeShared()
      case _ =>
        logger.warn("VM Scheduling algorithm should be either TimeShared or SpaceShared.")
        logger.info("Going ahead with TimeShared VM Scheduling.")
        new VmSchedulerTimeShared()
    })
  }

  def generateVMs (conf: ConfigReader): List[Vm] = {
    (1 to conf.nVM).map(_ => new VmSimple(conf.mips, conf.pesVM).setRam(conf.ramVM).setBw(conf.BWVM).setBw(conf.BWVM)).toList
  }

  def generateCloudlets(conf: ConfigReader): List[Cloudlet] = {
    val utilModel = new UtilizationModelDynamic(conf.utilRatio)
    (1 to conf.nClet).map (_ => new CloudletSimple(conf.len, conf.pesClet, utilModel).setOutputSize(conf.opSize)).toList
  }

  def generateVM (conf: ConfigReader): Vm = {
    new VmSimple(conf.mips, conf.pesVM).setSize(conf.sizeVM).setRam(conf.ramVM).setBw(conf.BWVM)
  }
  def generateScalableVms(conf: ConfigReader, n: Int): List[Vm] = 
    (1 to n).map { _ => 
      val vm: Vm = generateVM(conf)
      horizontalScaler(vm, conf)
      vm
  }.toList
  
  def horizontalScaler(vm: Vm, conf: ConfigReader): Unit = {
    val horizontalVmScaling: HorizontalVmScaling = new HorizontalVmScalingSimple()
    horizontalVmScaling.setVmSupplier(() => generateVM(conf)).setOverloadPredicate(isOverloaded)
    vm.setHorizontalScaling(horizontalVmScaling)
  }
  def isOverloaded(vm: Vm): Boolean = {
    vm.getCpuPercentUtilization > 0.2
  }

  def generateDatacenter (conf: ConfigReader, sim: CloudSim, schedInterval: Int = 0): Datacenter = {
    val hostList: List[Host] = (1 to conf.nHost).map(_ => generateHost(conf)).toList
    val dataCenter = conf.dType match {
      case "Simple" => new DatacenterSimple(sim, hostList.asJava).setSchedulingInterval(schedInterval)
      case "Network" => new NetworkDatacenter(sim, hostList.asJava, conf.getAllocPolicy).setSchedulingInterval(schedInterval)
      case _ =>
        logger.warn("Datacenter type should be either Simple or Network.")
        logger.info("Going ahead with creating a Simple Datacenter.")
        new DatacenterSimple(sim, hostList.asJava)
    }
    dataCenter.getCharacteristics.setCostPerBw(conf.costBW).setCostPerMem(conf.costMem).setCostPerSecond(conf.costSec).setCostPerStorage(conf.costStr)
    dataCenter

  }

  def findCost(broker: DatacenterBroker): Double = {
    var total: Double = 0.0
    for (cl <- broker.getVmCreatedList.asScala) {
      val cost: VmCost = new VmCost(cl)

      logger.info("Working on datacenter {}, {}", cl.getLastTriedDatacenter.getId, cost)
      total += cost.getTotalCost
    }
    total
  }

}
