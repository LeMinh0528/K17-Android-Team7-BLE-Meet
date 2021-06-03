package com.ceslab.team7_ble_meet.utils

import com.google.firebase.storage.FirebaseStorage
import java.util.*

object ImagesStorageUtils {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    var imageRef = storageInstance.reference.child("images")

    fun uploadProfilePhoto(imageBytes: ByteArray,
                           onSuccess: (imagePath: String) -> Unit) {
        val ref = imageRef.child("${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                onSuccess(ref.path)
            }
    }
    fun uploadMessagePhoto(imageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit){
        var ref = imageRef.child("${UUID.nameUUIDFromBytes(imageBytes)}")
            ref.putBytes(imageBytes)
                .addOnSuccessListener {
                    onSuccess(ref.path)
                }
    }

    fun pathToReference(path: String) = storageInstance.getReference(path)
}