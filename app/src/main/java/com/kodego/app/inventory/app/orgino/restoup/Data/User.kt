package com.kodego.app.inventory.app.orgino.restoup.Data

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
    var assignedRestaurant: String? = null

    constructor(userName:String,
                passWord:String,
                userType:UserTypes,
                providedUID:String,
                admin_UID:String) : this(userName, passWord, userType){
                    this.uID = providedUID
                    this.adminUID = admin_UID
                }

    //Employee Constructor
    constructor(employeeFirstName:String,
                employeeMiddleName:String,
                employeeLastName:String?,
                employeeBirthDate:LocalDate?,
                employeeEmail:String?,
                employeeUserName:String,
                employeeInitialPassWord:String,
                employeeUserType:UserTypes,
                employeeProvidedUID:String,
                employeeAdminUID:String,
                employeeAssignedRestaurant: String) : this(employeeUserName, employeeInitialPassWord, employeeUserType) {
                    this.firstName = employeeFirstName
                    this.middleName = employeeMiddleName
                    this.lastName = employeeLastName
                    this.birthDate = employeeBirthDate
                    this.email = employeeEmail
                    this.uID = employeeProvidedUID
                    this.adminUID = employeeAdminUID
                    this.assignedRestaurant = employeeAssignedRestaurant
                }

    constructor() : this("", "", UserTypes.CUSTOMER)
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
    var assignedRestaurant: String? = null

    constructor(employeeFirstName:String,
                employeeMiddleName:String,
                employeeLastName:String?,
                employeeBirthDate:LocalDate?,
                employeeEmail:String?,
                employeeUserName:String,
                employeeInitialPassWord:String,
                employeeUserType:UserTypes,
                employeeAdminUID:String,
                employeeAssignedRestaurant: String) :   this(employeeUserName, employeeInitialPassWord, employeeUserType) {
                                    this.firstName = employeeFirstName
                                    this.middleName = employeeMiddleName
                                    this.lastName = employeeLastName
                                    this.birthDate = employeeBirthDate.toString()
                                    this.email = employeeEmail
                                    this.adminUID = employeeAdminUID
                                    this.assignedRestaurant = employeeAssignedRestaurant
    }
}

enum class UserTypes() {
    ADMIN,
    CASHIER,
    WAITER,
    KITCHENSTAFF,
    CUSTOMER
}