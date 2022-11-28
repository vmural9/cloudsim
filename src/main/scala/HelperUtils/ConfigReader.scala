package HelperUtils

import com.typesafe.config.{Config, ConfigFactory}
import HelperUtils.{ConfigReader, CreateLogger, Parameters}
import org.cloudbus.cloudsim.allocationpolicies.*
import org.cloudbus.cloudsim.core

class ConfigReader(service: String,cli: String, dc: String) {
  val logger = CreateLogger(classOf[ConfigReader])

  val dcConf: Config = ConfigFactory.load(dc)
  val clientConf: Config = ConfigFactory.load(cli)

  //Read host config
  val hostConfig: Config = dcConf.getObject("datacenter.host").toConfig
  val nHost: Int = hostConfig.getInt("n")
  val pesHost: Int = hostConfig.getInt("pes")
  val mipsHost: Int = hostConfig.getInt("mips")
  val sizeHost: Int = hostConfig.getInt("size")
  val ramHost: Int = hostConfig.getInt("ram")
  val bwHost: Int = hostConfig.getInt("bw")
  val schedPolicy: String = hostConfig.getString("schedPolicy")

  //Read Datacenter Config
  val schedulingInterval: Int = dcConf.getInt("datacenter.schedulingInterval")
  val allocPolicy: String = dcConf.getString("datacenter.allocPolicy")
  val dType: String = dcConf.getString("datacenter.dType")
  val costSec: Double = dcConf.getDouble("datacenter.costSec")
  val costBW: Double = dcConf.getDouble("datacenter.costBW")
  val costMem: Double = dcConf.getDouble("datacenter.costMem")
  val costStr: Double = dcConf.getDouble("datacenter.costStr")



  //Different Services require a different combination of parameters from the client and the datacenter.
  //SaaS - client controls cloudlet config*
  //PaaS - client controls cloudlet config + no. of vms
  //Iaas - client controls cloudlet config + vm config.

  //Creating config files.
  val cloudletConf: Config = clientConf.getObject("client.cloudlet").toConfig //all services require atleast some cloudlet configuration from the clients
  val vmConf:Config = {
    if service == "iaas" then
      clientConf.getObject("client.vm").toConfig
    else
      dcConf.getObject("datacenter.vm").toConfig
  }

  //Extracting cloudlet parameters
  val nClet: Int = cloudletConf.getInt("n")
//  val minClet: Int = cloudletConf.getInt("min") wasn't used anywhere.
  val len: Int = cloudletConf.getInt("len")
  val opSize: Int = cloudletConf.getInt("opSize")
  val ramClet: Int = cloudletConf.getInt("ram")
  val utilRatio: Double = {
    if service == "saas" then
      logger.info("Cloudlet Pes from Datacenter")
      dcConf.getDouble("datacenter.utilRatio")
    else
      cloudletConf.getDouble("utilRatio")
  }
  val pesClet: Int = {
    if service == "saas" then
      logger.info("Cloudlet Pes from Datacenter")
      dcConf.getInt("datacenter.pes")

    else
      cloudletConf.getInt("pes")
  }

  //Extracting VM parameters Saas & PaaS read it from the dc whereas IaaS reads it from the client.
  val nVM: Int = {
    if service == "paas" then
      clientConf.getInt("client.vm.n")
    else
      vmConf.getInt("n")
  }
  val mips: Int = vmConf.getInt("mips")
  val sizeVM: Int = vmConf.getInt("size")
  val ramVM: Int = vmConf.getInt("ram")
  val BWVM: Int = vmConf.getInt("bw")
  val pesVM: Int = vmConf.getInt("pes")

  def getAllocPolicy: VmAllocationPolicy = {
    logger.info("Allotted Allocation Policy")
    allocPolicy match {
      case "FirstFit" => new VmAllocationPolicyFirstFit()
      case "BestFit" => new VmAllocationPolicyBestFit()
      case "RoundRobin" => new VmAllocationPolicyRoundRobin()
      case "Simple" => new VmAllocationPolicySimple()
      case _ => new VmAllocationPolicySimple()
    }
  }



}
