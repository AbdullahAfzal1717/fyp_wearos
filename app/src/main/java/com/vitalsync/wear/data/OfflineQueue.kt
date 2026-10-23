package com.vitalsync.wear.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Simple file-backed queue for when the watch is offline.
 * Flushed on the next successful stream tick.
 */
class OfflineQueue(context: Context) {
    private val file = File(context.filesDir, "vitals_queue.json")
    private val lock = Any()

    fun enqueue(item: VitalUploadRequest) = synchronized(lock) {
        val arr = readArray()
        arr.put(
            JSONObject()
                .put("heartRate", item.heartRate)
                .put("spo2", item.spo2)
                .put("temperature", item.temperature)
                .put("activityState", item.activityState)
                .put("timestamp", item.timestamp),
        )
        // Cap queue so disk does not grow forever
        while (arr.length() > 200) {
            arr.remove(0)
        }
        file.writeText(arr.toString())
    }

    fun size(): Int = synchronized(lock) { readArray().length() }

    fun drain(): List<VitalUploadRequest> = synchronized(lock) {
        val arr = readArray()
        val list = buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    VitalUploadRequest(
                        heartRate = o.getInt("heartRate"),
                        spo2 = o.getInt("spo2"),
                        temperature = o.getDouble("temperature"),
                        activityState = o.getString("activityState"),
                        timestamp = o.getString("timestamp"),
                    ),
                )
            }
        }
        file.writeText("[]")
        list
    }

    fun restore(items: List<VitalUploadRequest>) = synchronized(lock) {
        val arr = JSONArray()
        items.forEach { item ->
            arr.put(
                JSONObject()
                    .put("heartRate", item.heartRate)
                    .put("spo2", item.spo2)
                    .put("temperature", item.temperature)
                    .put("activityState", item.activityState)
                    .put("timestamp", item.timestamp),
            )
        }
        file.writeText(arr.toString())
    }

    private fun readArray(): JSONArray {
        if (!file.exists()) return JSONArray()
        return runCatching { JSONArray(file.readText()) }.getOrElse { JSONArray() }
    }
}
