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
	definition (name: "Tasmota IR Air Conditioner", namespace: "croko", author: "croko") {
		capability "Actuator"
		capability "Configuration"
		capability "Refresh"
		capability "Switch"
		capability "Thermostat" //Jorge
		capability "ThermostatCoolingSetpoint" //Ajustei
		capability "ThermostatHeatingSetpoint" //Ajustei
		capability "ThermostatMode"            //Ajustei
		capability "ThermostatFanMode"         //Ajustei
		capability "ThermostatOperatingState"         //Ajustei
	}

	preferences {
		input name: "TasmotaIP", title:"local IP address of Tasmota IR", type: "string", required: true
		input name: "username", title:"Username of Tasmota IR", type: "string"
		input name: "password", title:"Password of Tasmota IR", type: "string"
		input name: "ACvendor", title:"Vendor string of Air Conditioner", options: ["SAMSUNG_AC", "LG", "LG2", "COOLIX", "DAIKIN", "KELVINATOR", "MITSUBISHI_AC", "GREE", "ARGO", "TROTEC", "TOSHIBA_AC", "FUJITSU_AC", "MIDEA", "HAIER_AC", "HITACHI_AC", "HAIER_AC_YRW02", "WHIRLPOOL_AC", "ELECTRA_AC", "PANASONIC_AC", "DAIKIN2", "VESTEL_AC", "TECO", "TCL112AC", "MITSUBISHI_HEAVY_88", "MITSUBISHI_HEAVY_152", "DAIKIN216", "SHARP_AC", "GOODWEATHER", "DAIKIN160", "NEOCLIMA", "DAIKIN176", "DAIKIN128"], type: "enum", required: true, defaultValue: "SAMSUNG_AC"
	}
}

def parse(String description) {
}

def off() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"Off"}')
  sendEvent(name: "switch", value: "off", displayed: true)
  sendEvent(name: "off", value: "off", displayed: true)
	sendEvent(name: "thermostatMode", value: "off", displayed: true)
  sendEvent(name: "thermostatOperatingState", value: "idle", displayed: true)
}

def on() {
	if (MODE == "heat" || MODE == "Heat") {
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+MODE+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("heatingSetpoint")+'"}')
	}
	else {
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+MODE+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'"}')
	}
	sendEvent(name: "thermostatMode", value: "auto", displayed: true)
  sendEvent(name: "switch", value: "on", displayed: true)
  sendEvent(name: "on", value: "on", displayed: true)
}

def setCoolingSetpoint(Double temperature){
	temperature =	temperature.toInteger()
	if (state.switch=="on") {
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Cool","FanSpeed":"'+FANMODE+'","Temp":"'+temperature+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		sendEvent(name: "coolingSetpoint", value: temperature as int, unit: "C", displayed: true)
		sendEvent(name: "thermostatSetpoint", value: temperature as int, unit: "C", displayed: true)
	}
}

def setHeatingSetpoint(Double temperature){
	temperature =	temperature.toInteger()
	if (state.switch=="on") {
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Heat","FanSpeed":"'+FANMODE+'","Temp":"'+temperature+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		sendEvent(name: "heatingSetpoint", value: temperature as int, unit: "C", displayed: true)
		sendEvent(name: "thermostatSetpoint", value: temperature as int, unit: "C", displayed: true)
	}
}

def heat() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Heat","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("heatingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
  sendEvent(name: "thermostatMode", value: "heat", displayed: true)
	sendEvent(name: "switch", value: "on", displayed: true)
  sendEvent(name: "on", value: "on", displayed: true)
  sendEvent(name: "currentmode", value: "heat", displayed: true)
  sendEvent(name: "thermostatOperatingState", value: "heating", displayed: true)
}

def cool() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Cool","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
  sendEvent(name: "thermostatMode", value: "cool", displayed: true)
  sendEvent(name: "on", value: "on", displayed: true)
	sendEvent(name: "switch", value: "on", displayed: true)
  sendEvent(name: "currentmode", value: "cool", displayed: true)
  sendEvent(name: "thermostatOperatingState", value: "cooling", displayed: true)
}

def auto() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
  sendEvent(name: "thermostatMode", value: "auto", displayed: true)
  sendEvent(name: "on", value: "on", displayed: true)
  sendEvent(name: "currentmode", value: "auto", displayed: true)
}

def fanAuto() {
		if (MODE == "heat" || MODE == "Heat") {
			sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"Auto","Temp":"'+device.currentValue("heatingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		}
		else {
			sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"Auto","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		}
  sendEvent(name: "thermostatFanMode", value: "auto", displayed: true)
}

def fanCirculate() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"Low","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
  sendEvent(name: "thermostatFanMode", value: "min", displayed: true)
  sendEvent(name: "fanLevel", value: "min", displayed: true)
}

def fanOn() {
	sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"Auto","FanSpeed":"Max","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
  sendEvent(name: "thermostatFanMode", value: "max", displayed: true)
  sendEvent(name: "fanLevel", value: "max", displayed: true)
}

def setThermostatMode(mode) {
	if (mode == "off") {
		sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"Off","Mode":"'+mode+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		sendEvent(name: "switch", value: "off", displayed: true)
		sendEvent(name: "off", value: "off", displayed: true)
		sendEvent(name: "thermostatOperatingState", value: "idle", displayed: true)
	}
	else {
				if (mode == "heat" || mode == "Heat") {
					sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+mode+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("heatingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
				}
				else {
					sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+mode+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
				}
		sendEvent(name: "on", value: "on", displayed: true)
		sendEvent(name: "switch", value: "on", displayed: true)
		sendEvent(name: "thermostatMode", value: mode, displayed: true)
		sendEvent(name: "currentmode", value: mode, displayed: true)

		if (mode == "heat" || mode == "Heat" || mode == "dry") {
			sendEvent(name: "thermostatOperatingState", value:"heating", displayed: true)
		}
		else if (mode == "cool" || mode == "Cool") {
			sendEvent(name: "thermostatOperatingState", value:"cooling", displayed: true)
		}
		else {
			sendEvent(name: "thermostatOperatingState", value: mode, displayed: true)
		}
	}
}

def setThermostatFanMode(mode) {
  if (mode!="auto" && mode!="circulate" && mode!="on") {
		sendEvent(name: "thermostatFanMode", value: mode, displayed: true)
  	sendEvent(name: "fanLevel", value: mode, displayed: true)
		if (MODE == "heat" || MODE == "Heat") {
			sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+device.currentValue("thermostatMode")+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("heatingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		}
		else {
			sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+device.currentValue("thermostatMode")+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		}
	} else {
		sendEvent(name: "thermostatFanMode", value: mode, displayed: true)
  	sendEvent(name: "fanLevel", value: mode, displayed: true)
		if (MODE == "heat" || MODE == "Heat") {
			sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+device.currentValue("thermostatMode")+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("heatingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		}
		else {
			sendTasmota('IRhvac {"Vendor":"'+VENDOR+'", "Power":"On","Mode":"'+device.currentValue("thermostatMode")+'","FanSpeed":"'+FANMODE+'","Temp":"'+device.currentValue("coolingSetpoint")+'","SwingV":"off","SwingH":"off","Quiet":"off","Turbo":"off","Econo":"off","Light":"on","Filter":"on","Clean":"on","Beep":"off","Sleep":-1}')
		}
	}
}

def refresh() {
	log.debug "refresh() called"
	installed()
}

def configure() {
	log.debug "in configure()"
}

def installed() {
	log.debug "in installed()"
	state.switch="off"
	state.switch="on"  // JORGE
	sendEvent(name: "switch", value: "off", displayed: true)
	sendEvent(name: "switch", value: "on", displayed: true)  // JORGE
	sendEvent(name: "heatingSetpoint", value: 18, unit: "C", displayed: true)
	sendEvent(name: "coolingSetpoint", value: 21, unit: "C", displayed: true)
	sendEvent(name: "thermostatSetpoint", value: 23, unit: "C", displayed: true)
	// sendEvent(name: "thermostatMode", value: "heat", displayed: true)
	// sendEvent(name: "thermostatMode", value: "cool", displayed: true)
	sendEvent(name: "thermostatFanMode", value: "auto", displayed: true)
	sendEvent(name: "supportedThermostatModes", value:["auto", "heat", "cool","dry","fan", "off"])
	sendEvent(name: "supportedThermostatFanModes", value:["auto", "min", "low", "med", "high", "max"])
}

def updated() {
	log.debug "in updated()"
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
