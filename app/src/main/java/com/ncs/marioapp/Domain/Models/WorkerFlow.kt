package com.ncs.marioapp.Domain.Models

import android.net.Uri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.serialization.Serializable

@Serializable
data class WorkerFlow(
    val userImageUri: String = "",
    val collegeIdUri: String = "",
    val createProfilePayload: CreateProfileBody = CreateProfileBody(),
    val isUpdate: Boolean = false,
)

class UriTypeAdapter : TypeAdapter<Uri>() {
    override fun write(out: JsonWriter, value: Uri?) {
        out.value(value?.toString())
    }

    override fun read(input: JsonReader): Uri? {
        val uriString = input.nextString()
        return Uri.parse(uriString)
    }
}
