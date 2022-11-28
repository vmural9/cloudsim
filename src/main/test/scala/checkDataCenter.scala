

import HelperUtils.helperfunctions.*
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class testDatacenter extends AnyFlatSpec with Matchers {
  behavior of "Check if Datacenter is created "
  it should "create a datacenter" in {
    val sim = new CloudSim()
    val conf = new ConfigReader("iaas", "client.conf", "datacenter.conf")
    val DC = generateDatacenter(conf, sim)

    DC shouldBe a [Datacenter]
  }
}