/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.io.Log
import java.io.DataInput
import java.io.DataOutput
import java.util.*

/**
 * Base class for all structures defined in the class file format.
 *
 *
 * Provides common services such as reading, writing and debugging.
 */

//TODO check all "factory methods"
abstract class Structure {

    /**
     * Read this structure from the given DataInput.
     * Expects DataInput to be in JVM class file format and just
     * before a structure of this kind. No look ahead parsing since
     * the class file format is deterministic.

     * @param input the DataInput from which to read
     */
    fun read(input: DataInput) {
        readData(input)
        if (isDebug) debug("read ${javaClass.simpleName} $debugInfo")
    }

    protected abstract fun readData(input: DataInput)

    /**
     * Write this structure to the given DataOutput.
     * The written bytes are in JVM class file format.

     * @param output the DataOutput to which to write
     */
    fun write(output: DataOutput) {
        writeData(output)
        if (isDebug) debug("wrote ${javaClass.simpleName} $debugInfo")
    }

    protected abstract fun writeData(output: DataOutput)

    /**
     * Utility method for derived structures. Dump a specific debug message.
     * @param message the debug message
     */
    protected fun debug(message: String) {
        if (isDebug) Log.debug(message)
    }

    protected abstract val debugInfo: String

    /**
     * Utility method for derived structures. Print an int value as a hex string.
     * @param bytes the int value to print as a hex string
     * @return the hex string
     */
    protected fun printBytes(bytes: Int): String {
        return padHexString(Integer.toHexString(bytes), 8)
    }

    /**
     * Utility method for derived structures. Print an access flag or an
     * unsigned short value as a hex string.
     * @param accessFlags the unsigned short value to print as a hex string
     * @return the hex string
     */
    protected fun printAccessFlags(accessFlags: Int): String {
        return padHexString(Integer.toHexString(accessFlags), 4)
    }

    private fun padHexString(hexString: String, length: Int): String {
        return "0x" + hexString.padStart(length, '0')
    }

    /**
     * Utility method for derived structures. Print an access flag as
     * a space separated list of verbose java access modifiers.
     * @param availableAccessFlags array with the access flags available for the derived structure
     * @param accessFlags the unsigned short value to print as a hex string
     * @return the access flags verbose description
     */
    protected fun printAccessFlagsVerbose(availableAccessFlags: EnumSet<AccessFlag>, accessFlags: Int): String {

        val matchingFlags = availableAccessFlags.filter { (accessFlags and it.flag) != 0 }
        val handledFlags = matchingFlags.fold(0) {value, accessFlag -> value or accessFlag.flag}

        return matchingFlags.
                mapNotNull { if (it.verbose.isEmpty()) null else it.verbose }.
                joinToString(separator = " ", postfix = if (accessFlags != handledFlags) "?" else "")
    }

    companion object {

        /**
         * Set this JVM System property to true to switch on debugging for
         * reading and writing class files.
         */
        const val SYSTEM_PROPERTY_DEBUG = "jclasslib.io.debug"

        /**
         * Flag for debugging while reading and writing class files.
         */
        val isDebug: Boolean = java.lang.Boolean.getBoolean(SYSTEM_PROPERTY_DEBUG)
    }

}
