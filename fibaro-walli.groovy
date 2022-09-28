/**
 *
 *	Fibaro Walli Controller
 * Based on the driver by Chris Charles
 *	Modified by Gennady Kudelya
 *	Date: 2022-09-28
 */

metadata {
definition (name: "Fibaro Walli", namespace: "croko", author: "Chris (help from Eric and Robin)") {
    capability 'Battery'
    capability 'Configuration'
    capability 'HoldableButton'
    capability 'PushableButton'
    capability 'ReleasableButton'
    capability "Refresh"
    capability "Sensor"
    capability "Temperature Measurement"
    capability "TemperatureMeasurement"

command "updateSingleParam" // This custom command can be used with Rule Machine or webCoRE, to send parameter values (paramNr & paramvalue) to the device

// device type 516 = 0x0204, manufacturer 271 = 0x010F, 4096 = 0x1000
// MSR: 010F-2301-1000
fingerprint mfr:"010F", prod:"1B01", deviceId: "1000", inClusters:"0x5E,0x85,0x8E,0x59,0x87,0x55,0x86,0x72,0x5A,0x73,0x98,0x9F,0x70,0x7A,0x5B,0x31,0x22,0x75,0x6C"

}

preferences {
    input name: "enableDebugging", type: "bool", title: "Enable Debug logging", defaultValue: false
	input(name: "tempOffset", type: "decimal", title: "Temperature Offset", description: "Adjust the temperature by this many degrees.", displayDuringSetup: true, required: false, range: "-5..5")
    }
}

def parse(String description)
{
    def result = []
    def cmd = zwave.parse(description)
    if (cmd)
    {
        result += zwaveEvent(cmd)
        logging "Parsed ${cmd} to ${result.inspect()}"
    }
    else
    {
        logging "Non-parsed event: ${description}"
    }
    return result
}

def zwaveEvent(hubitat.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
    logging("button pushed ${cmd.sceneNumber}")
    sendEvent(name: 'pushed', value: cmd.sceneNumber.toInteger(), isStateChange: true)
}

def zwaveEvent(hubitat.zwave.commands.sensormultilevelv11.SensorMultilevelReport cmd)
{
log.debug "hubitat.zwave.commands.sensormultilevelv11.SensorMultilevelReport ${cmd}"
log.debug "offset ${settings.tempOffset.toFloat().round(2)}"

    state.sensorTemperature = cmd.scaledSensorValue
    state.tempOffset = settings.tempOffset.toFloat().round(2)
    state.realTemperature = cmd.scaledSensorValue + state.tempOffset
    def map = [ displayed: true, value: state.realTemperature.toString() ]
    map.name = "temperature"
    map.unit = cmd.scale == 1 ? "F" : "C"
	createEvent(map)
}


def zwaveEvent(hubitat.zwave.Command cmd) {
    // This will capture any commands not handled by other instances of zwaveEvent
    // and is recommended for development so you can see every command the device sends
    log.debug "catchall ${cmd}"
    return createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

def installed() {
    logging("installed()")
    configure()
}

def updated() {
    state.enableDebugging = settings.enableDebugging
    logging("updated()")
    configure()
}

def configure() {
	logging("configure tempOffset = ${settings.tempOffset}")
    state.tempOffset = settings.tempOffset.toFloat().round(2)
    sendEvent(name: "numberOfButtons", value: 2, displayed: true)
    sendEvent(name: "temperature", value: state.realTemperature, displayed: true)
    state.isConfigured = "true"
}

def refresh() {
    logging("refresh()")
    state.tempOffset = settings.tempOffset.toFloat().round(2)
    zwave.sensorMultilevelV11.sensorMultilevelGet(scale: 0, sensorType: 1).format()
}

private def logging(message) {
    if (state.enableDebugging == true) log.debug message
}