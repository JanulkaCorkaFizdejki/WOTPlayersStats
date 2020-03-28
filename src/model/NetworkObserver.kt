package model

import DataModel.INTERNET_ACCESS

interface NetworkObserver {
    fun update(internetAccess: INTERNET_ACCESS)
}