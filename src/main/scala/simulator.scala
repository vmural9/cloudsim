

/*
Saas Datacenter Simulation
Client: Control over software service - size and number of cloudlet - saasclient.conf
The datacenter i.e. the cloud provider controls the rest - datacenter.conf
*/

import java.util
import scala.jdk.CollectionConverters.*
import HelperUtils.{ConfigReader, CreateLogger, Parameters}
import HelperUtils.helperfunctions.*
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder


object simulator {
  val logger = CreateLogger(classOf[simulator.type])

  def serviceSim(service: String, cli: String, dc: String): Unit = {
    logger.info("SaaS Simulation - Starting...")

    val conf = new ConfigReader(service,cli,dc)

    val sim = new CloudSim()

    generateDatacenter(conf, sim)

    val broker = new DatacenterBrokerSimple(sim)

    broker.submitVmList(generateVMs(conf).asJava)
    broker.submitCloudletList(generateCloudlets(conf).asJava)

    sim.start()

    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val createdVM = broker.getVmCreatedList()
    logger.info(s"Created VM list: $createdVM")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val CloudCreatedList = broker.getCloudletCreatedList()
    logger.info(s"List of cloudlets created inside some Vm.: $CloudCreatedList")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val finishedCloudlets: util.List[Cloudlet] = broker.getCloudletFinishedList()
    logger.info(s"Finished Cloudlet list: $finishedCloudlets")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    logger.info("Total cost of simulation = {} $", findCost(broker))
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    //Uses the CloudletsTableBuilder class to build a tabular result
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()

  }
  def dcnetwork(): Unit = {

    val sim = new CloudSim()

    val iaasConf = new ConfigReader("iaas", "client.conf" ,"datacenter.conf")
    val paasConf = new ConfigReader("paas", "client.conf", "datacenter.conf")
    val saasConf = new ConfigReader("saas", "client.conf", "datacenter.conf")

    val dcPaas = generateDatacenter(paasConf, sim)
    val dcIaas = generateDatacenter(iaasConf, sim)
    val dcSaas = generateDatacenter(saasConf, sim)

    val broker = new DatacenterBrokerSimple(sim)

    val networkTopology: BriteNetworkTopology = BriteNetworkTopology.getInstance("topology.brite")
    sim.setNetworkTopology(networkTopology)

    networkTopology.mapNode(dcIaas, 0)
    networkTopology.mapNode(dcPaas, 2)
    networkTopology.mapNode(dcSaas, 3)
    networkTopology.mapNode(broker, 4)

    //Adding vms from different services to the vm list
    val vms: util.List[Vm] = new util.ArrayList[Vm]
    vms.addAll(generateVMs(iaasConf).asJava)
    vms.addAll(generateVMs(paasConf).asJava)
    vms.addAll(generateVMs(saasConf).asJava)

    //Adding cloudlets from different clients to the cloulet list
    val cloudlets: util.List[Cloudlet] = new util.ArrayList[Cloudlet]
    cloudlets.addAll(generateCloudlets(iaasConf).asJava)
    cloudlets.addAll(generateCloudlets(paasConf).asJava)
    cloudlets.addAll(generateCloudlets(saasConf).asJava)

    broker.submitVmList(vms)
    broker.submitCloudletList(cloudlets)
    sim.start()

    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val createdVM = broker.getVmCreatedList()
    logger.info(s"Created VM list: $createdVM")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val CloudCreatedList = broker.getCloudletCreatedList()
    logger.info(s"List of cloudlets created inside some Vm.: $CloudCreatedList")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val finishedCloudlets: util.List[Cloudlet] = broker.getCloudletFinishedList()
    logger.info(s"Finished Cloudlet list: $finishedCloudlets")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    logger.info("Total cost of simulation = {} $", findCost(broker))
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    //Uses the CloudletsTableBuilder class to build a tabular result
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()
  }

  def scalingServiceSim(service: String, cli: String, dc: String):Unit = {
    logger.info("Scaling SaaS Simulation - Starting...")

    val conf = new ConfigReader(service, cli, dc)

    val sim = new CloudSim()

    generateDatacenter(conf, sim, conf.schedulingInterval)

    val broker = new DatacenterBrokerSimple(sim)
    broker.setVmDestructionDelay(10.0)

    broker.submitVmList(generateScalableVms(conf,conf.nVM).asJava)
    broker.submitCloudletList(generateCloudlets(conf).asJava)

    sim.start()

    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val createdVM = broker.getVmCreatedList()
    logger.info(s"Created VM list: $createdVM")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val CloudCreatedList = broker.getCloudletCreatedList()
    logger.info(s"List of cloudlets created inside some Vm.: $CloudCreatedList")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    val finishedCloudlets: util.List[Cloudlet] = broker.getCloudletFinishedList()
    logger.info(s"Finished Cloudlet list: $finishedCloudlets")
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    logger.info("Total cost of simulation = {} $", findCost(broker))
    System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n")

    //Uses the CloudletsTableBuilder class to build a tabular result
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()

  }


  def main(args: Array[String]): Unit = {
//    serviceSim("iaas", "client.conf", "datacenter.conf")
//    dcnetwork()
    scalingServiceSim("paas", "client.conf", "datacenter.conf")
  }
}
