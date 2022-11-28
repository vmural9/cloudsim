
import HelperUtils.helperfunctions.*
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}

class testDatacenter extends AnyFlatSpec with Matchers {
  behavior of "Check if Cloudlet is created "
  it should "create a cloudlet" in {
    val sim = new CloudSim()
    val conf = new ConfigReader("iaas", "client.conf", "datacenter.conf")
    val cloudlets = generateCloudlets(conf)
    cloudlets shouldBe a [Cloudlet]
  }
}