/**
 *  Tasmota IR Air Conditioner 0.0.1.1 (alpha preview)
 *	Copyright 2020 Jaewon Park
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Tasmota IR Air Conditioner", namespace: "iquix", author: "iquix", ocfDeviceType: "oic.d.airconditioner") {
		capability "Actuator"
		capability "Configuration"
		capability "Refresh"
		capability "Switch"
		capability "Thermostat" //Jorge
		capability "ThermostatCoolingSetpoint" //Ajustei
		capability "ThermostatHeatingSetpoint" //Ajustei
		capability "ThermostatMode"            //Ajustei
		capability "ThermostatFanMode"         //Ajustei
		capability "HealthCheck"               //Ajustei
		//attribute "power", "number"
	}

	// fingerprint profileId: "0104", deviceId: "0051", inClusters: "0000 0003 0004 0006 0009 0702 0B04", outClusters: "0000 0003 0004 0006 0009 0702 0B04", manufacturer: "Heiman", model: "SmartPlug", deviceJoinName: "에어컨" // fingerprint of Heiman 16A plug

	preferences {
		input name: "TasmotaIP", title:"local IP address of Tasmota IR", type: "string", required: true
		input name: "username", title:"Username of Tasmota IR", type: "string"
		input name: "password", title:"Password of Tasmota IR", type: "string"
		input name: "ACvendor", title:"Vendor string of Air Conditioner", options: ["SAMSUNG_AC", "LG", "LG2", "COOLIX", "DAIKIN", "KELVINATOR", "MITSUBISHI_AC", "GREE", "ARGO", "TROTEC", "TOSHIBA_AC", "FUJITSU_AC", "MIDEA", "HAIER_AC", "HITACHI_AC", "HAIER_AC_YRW02", "WHIRLPOOL_AC", "ELECTRA_AC", "PANASONIC_AC", "DAIKIN2", "VESTEL_AC", "TECO", "TCL112AC", "MITSUBISHI_HEAVY_88", "MITSUBISHI_HEAVY_152", "DAIKIN216", "SHARP_AC", "GOODWEATHER", "DAIKIN160", "NEOCLIMA", "DAIKIN176", "DAIKIN128"], type: "enum", required: true, defaultValue: "SAMSUNG_AC"

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', icon:"st.switches.switch.on", backgroundColor:"#00A0DC"
				attributeState "off", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff"
			}
			tileAttribute ("power", key: "SECONDARY_CONTROL") {
				attributeState "power", label:'${currentValue} W'
			}
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main "switch"
		details(["switch", "refresh"])
	}
}

def parse(String description) {
}

def off() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"Off"}')
    sendEvent(name: "switch", value: "off", displayed: true)
}

def on() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+MODE+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'"}')
	sendEvent(name: "airConditionerMode", value: "auto", displayed: true)
    sendEvent(name: "switch", value: "on", displayed: true)
}

def setCoolingSetpoint(temperature){
	if (state.switch=="on") {
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Cool","FanSpeed":"'+FANMODE+'","Temp":"'+temperature+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		sendEvent(name: "coolingSetpoint", value: temperature as int, unit: "C", displayed: true)
	}
}

def setHeatingSetpoint(temperature){
	if (state.switch=="on") {
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Heat","FanSpeed":"'+FANMODE+'","Temp":"'+temperature+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		sendEvent(name: "heatingSetpoint", value: temperature as int, unit: "C", displayed: true)
	}
}

def heat() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Heat","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("heatingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
   	sendEvent(name: "airConditionerMode", value: "heat", displayed: true)
}

def cool() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Cool","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
    	sendEvent(name: "airConditionerMode", value: "cool", displayed: true)
}

def auto() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
    sendEvent(name: "airConditionerMode", value: "auto", displayed: true)
}


def fanAuto() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"Auto","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
    	sendEvent(name: "fanMode", value: "auto", displayed: true)
}

def fanCirculate() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"Low","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
    	sendEvent(name: "fanMode", value: "min", displayed: true)
}

def fanOn() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"Max","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
    sendEvent(name: "fanMode", value: "max", displayed: true)
}

//fim edição

def setThermostatMode(mode) {
    //"heat", "cool", "emergency heat", "auto", "off"
	// if (mode!="cool" && mode!="auto" && mode!="fanOnly" && mode!="dry") {
  if (mode!="cool" && mode!="auto" && mode!="heat" && mode!="emergency heat" && mode!="dry") { //editei aqui jorge
		sendEvent(name: "airConditionerMode", value: "auto", displayed: true)
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+mode+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
	} else {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+mode+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
	sendEvent(name: "airConditionerMode", value: mode, displayed: true)
	}
}

def setThermostatFanMode(mode) {
    //"auto", "circulate", "on"
	//if (mode!="low" && mode!="medium" && mode!="high" && mode!="auto") {
  if (mode!="auto" && mode!="circulate" && mode!="on") {
		sendEvent(name: "fanMode", value: "auto", displayed: true)
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+device.currentValue("thermostatMode")+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
	} else {
		sendEvent(name: "fanMode", value: mode, displayed: true)
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+device.currentValue("thermostatMode")+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
	}
}

def refresh() {
	log.debug "refresh() called"
}

def configure() {
	log.debug "in configure()"
}

def configureHealthCheck() {
	Integer hcIntervalMinutes = 12
	sendEvent(name: "checkInterval", value: hcIntervalMinutes * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
	return refresh()
}

def installed() {
	log.debug "in installed()"
	state.switch="off"
	state.switch="on"  // JORGE
	sendEvent(name: "switch", value: "off", displayed: true)
	sendEvent(name: "switch", value: "on", displayed: true)  // JORGE
	sendEvent(name: "coolingSetpoint", value: 24, unit: "C")
	sendEvent(name: "heatingSetpoint", value: 23, unit: "C")
	sendEvent(name: "supportedAcModes", value:["auto", "heat", "cool","dry","fanOnly"])
	sendEvent(name: "supportedAcFanModes", value:["auto", "low", "medium", "high", "turbo"])
	sendEvent(name: "airConditionerMode", value: "auto", displayed: false)
}

def updated() {
	log.debug "in updated()"
	// updated() doesn't have it's return value processed as hub commands, so we have to send them explicitly
	// def cmds = configure()
	// cmds.each{ sendHubCommand(new hubitat.device.HubAction(it)) } //Alterei
}

def ping() {
	// return zigbee.onOffRefresh() + zigbee.simpleMeteringPowerRefresh() + zigbee.electricMeasurementPowerRefresh()
}

def turnPlugOn() {
	// log.debug "Automatically turning on the zigbee plug"
	// def cmds = zigbee.on()
	// cmds.each{ sendHubCommand(new hubitat.device.HubAction(it)) } //Alterei
}

def sendTasmota(command) {
	def options = [
		method: "GET",
		headers: [HOST: settings.TasmotaIP+":80"],
		path: "/cm?user=" + (settings.username ?: "") + "&password=" + (settings.password ?: "") + "&cmnd=" + URLEncoder.encode(command, "UTF-8").replaceAll(/\+/,'%20')
	]
	log.debug options
	def hubAction = new hubitat.device.HubAction(options, null) //Alterei
	sendHubCommand(hubAction)
}

def getVENDOR() {
	return (settings.ACvendor ?: "SAMSUNG_AC")
}

def getFANMODE() {
	return (device.currentValue("thermostatFanMode") == "turbo" ? "max" : device.currentValue("thermostatFanMode"))
}
