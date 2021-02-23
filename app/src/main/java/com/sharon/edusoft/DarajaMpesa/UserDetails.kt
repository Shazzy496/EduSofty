package com.sharon.edusoft.DarajaMpesa

class UserDetails {
    var id: String = ""
    var amount: String = ""
    var phone: String = ""
    var date: String = ""
    var receipt: String = ""


    constructor()
    constructor(id: String,amount: String, phone: String, date: String, receipt: String) {
        this.id = id
        this.amount = amount
        this.phone = phone
        this.date = date
        this.receipt = receipt

    }


}