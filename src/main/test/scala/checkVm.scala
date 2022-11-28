
import HelperUtils.helperfunctions.*
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}

class testDatacenter extends AnyFlatSpec with Matchers {
  behavior of "Check if VM is created "
  it should "create a VM" in {
    val sim = new CloudSim()
    val conf = new ConfigReader("iaas", "client.conf", "datacenter.conf")
    val cloudlets = generateVM(conf)
    cloudlets shouldBe a [Cloudlet]
  }
}