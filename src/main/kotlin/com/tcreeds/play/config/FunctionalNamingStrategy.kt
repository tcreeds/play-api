package com.tcreeds.play.config

import org.hibernate.cfg.DefaultNamingStrategy

class FunctionalNamingStrategy : DefaultNamingStrategy(

) {
    override fun propertyToColumnName(propertyName: String?): String {
        return propertyName!!
    }
}