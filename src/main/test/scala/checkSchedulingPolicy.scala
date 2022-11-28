import HelperUtils.helperfunctions.*
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class testDatacenter extends AnyFlatSpec with Matchers {
  behavior of "Check if Scheduling policy is valid"
  it should "set SchedulePolicy" in {
    val sim = new CloudSim()
    val conf = new ConfigReader("iaas", "client.conf", "datacenter.conf")
    val DC = conf.schedPolicy

    DC shouldBe "TimeShared" or "SpaceShared"
  }
}