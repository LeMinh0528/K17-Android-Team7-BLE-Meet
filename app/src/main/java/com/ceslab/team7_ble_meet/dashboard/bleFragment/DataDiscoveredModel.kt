package com.ceslab.team7_ble_meet.dashboard.bleFragment

import com.ceslab.team7_ble_meet.ble.Characteristic

class DataDiscoveredModel(listChar: List<Int>) {
    val ID = listChar[0]
    val description = "Age: ${listChar[1]}\n" +
            "Sex: ${Characteristic.sex[listChar[2]]}\n" +
            "Sexuality Orientation: ${Characteristic.sexualOrientation[listChar[3]]}\n" +
            "Tall: ${listChar[4]}\n" +
            "Weight: ${listChar[5]}\n" +
            "Zodiac: ${Characteristic.zodiac[listChar[6]]}\n" +
            "Outlook: ${Characteristic.outlook[listChar[7]]} - ${Characteristic.outlook[listChar[8]]} - ${Characteristic.outlook[listChar[9]]}\n" +
            "Job: ${Characteristic.job[listChar[10]]} - ${Characteristic.job[listChar[11]]} - ${Characteristic.job[listChar[12]]}\n" +
            "Character: ${Characteristic.character[listChar[13]]} - ${Characteristic.character[listChar[14]]} - ${Characteristic.character[listChar[15]]}\n" +
            "Favorite: ${Characteristic.hightlight[listChar[16]]} - ${Characteristic.hightlight[listChar[17]]} - ${Characteristic.hightlight[listChar[18]]}\n" +
            "Hightlight: ${Characteristic.hightlight[listChar[19]]} - ${Characteristic.hightlight[listChar[20]]} - ${Characteristic.hightlight[listChar[21]]}\n"
}