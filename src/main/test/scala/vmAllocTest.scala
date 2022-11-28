

import HelperUtils.helperfunctions.*
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class testDatacenter extends AnyFlatSpec with Matchers {
  behavior of "Check for valid allocation policy "
  it should "Set Allocation policy" in {
    val conf = new ConfigReader("iaas", "client.conf", "datacenter.conf")
    val policy = conf.getVmAllocPolicy()
    policy shouldBe a [VmAllocationPolicy]
  }
}
