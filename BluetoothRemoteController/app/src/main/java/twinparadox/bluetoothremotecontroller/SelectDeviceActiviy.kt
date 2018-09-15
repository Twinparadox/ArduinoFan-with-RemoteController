package twinparadox.bluetoothremotecontroller

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast

class SelectDeviceActiviy : AppCompatActivity() {

    private var m_bluetoothAdapter:BluetoothAdapter? = null
    private lateinit var m_pairedDevices:Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(m_bluetoothAdapter == null) {
            toast("This device dosen't support bluetooth")
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)

        }

        select_device_refresh.setOnClickListener{ pairedDeviceList() }

    }

    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val deviceList : ArrayList<BluetoothDevice> = ArrayList()
        val nameList : ArrayList<String> = ArrayList()

        if(!m_pairedDevices.isEmpty()) {
            for(device:BluetoothDevice in m_pairedDevices) {
                deviceList.add(device)
                nameList.add(device.name+"("+device.address+")")
                Log.i("device",""+device.name)
            }
        }
        else {
            toast("Paired devices not found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,nameList)
        select_device_list.adapter = adapter

        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = deviceList[position]
            val address: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if(resultCode == Activity.RESULT_OK) {
                if(m_bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled")
                }
                else {
                    toast("Bluetooth has benn disabled")
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth enabling has benn canceled")
            }

        }
    }
}
