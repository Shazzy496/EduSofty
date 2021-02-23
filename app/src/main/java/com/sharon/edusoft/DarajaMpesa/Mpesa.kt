package com.sharon.edusoft.DarajaMpesa

class Mpesa {
    var id: String = ""
    var receipt: String = ""

    constructor()

    constructor(id: String, receipt: String) {
        this.id = id
        this.receipt = receipt
    }


}