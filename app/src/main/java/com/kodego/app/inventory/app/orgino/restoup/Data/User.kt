package com.kodego.app.inventory.app.orgino.restoup.Data

import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

data class User (
    var userName:String,
    var passWord:String,
    var userType:UserTypes
) {
    var uID:String? = null
    var firstName:String? = null
    var middleName:String? = null
    var lastName:String? = null
    var birthDate:LocalDate? = null
    var email:String? = null
    var adminUID:String? = null

    constructor(userName:String,
                passWord:String,
                userType:UserTypes,
                providedUID:String) : this(userName, passWord, userType){this.uID=providedUID}

    //Employee Constructor
    constructor(employeeFirstName:String,
                employeeMiddleName:String,
                employeeLastName:String?,
                employeeBirthDate:LocalDate?,
                employeeEmail:String?,
                employeeUserName:String,
                employeeInitialPassWord:String,
                employeeUserType:UserTypes,
                employeeAdminUID:String) : this(employeeUserName, employeeInitialPassWord, employeeUserType) {
        this.firstName = employeeFirstName
        this.middleName = employeeMiddleName
        this.lastName = employeeLastName
        this.birthDate = employeeBirthDate
        this.email = employeeEmail
        this.adminUID = employeeAdminUID
                }
}

data class ConvertedUser (
    var userName:String,
    var passWord:String,
    var userType:UserTypes
) {
    var uID: String? = null
    var firstName: String? = null
    var middleName: String? = null
    var lastName: String? = null
    var birthDate: String? = null
    var email: String? = null
    var adminUID: String? = null

    constructor(employeeFirstName:String,
                employeeMiddleName:String,
                employeeLastName:String?,
                employeeBirthDate:LocalDate?,
                employeeEmail:String?,
                employeeUserName:String,
                employeeInitialPassWord:String,
                employeeUserType:UserTypes,
                employeeAdminUID:String) : this(employeeUserName, employeeInitialPassWord, employeeUserType) {
        this.firstName = employeeFirstName
        this.middleName = employeeMiddleName
        this.lastName = employeeLastName
        this.birthDate = employeeBirthDate.toString()
        this.email = employeeEmail
        this.adminUID = employeeAdminUID
    }
}
enum class UserTypes() {
    ADMIN,
    CASHIER,
    WAITER,
    KITCHENSTAFF,
    CUSTOMER
}