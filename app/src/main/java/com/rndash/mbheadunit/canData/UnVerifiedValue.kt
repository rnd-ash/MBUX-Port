package com.rndash.mbheadunit.canData


/**
 * Marks an ECU Value that is unverified or may not be displayed correctly
 */
@Suppress("DEPRECATION")
@Experimental(level = Experimental.Level.WARNING)
@Target( AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class UnVerifiedValue