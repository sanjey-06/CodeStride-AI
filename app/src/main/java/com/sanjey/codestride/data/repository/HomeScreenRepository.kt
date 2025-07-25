package com.sanjey.codestride.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.common.getIconResource
import com.sanjey.codestride.data.model.Quote
import com.sanjey.codestride.data.model.RoadmapUI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeScreenRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // ✅ Observe Quotes in Real-Time
    fun observeQuotes(): Flow<List<Quote>> = callbackFlow {
        val listener = firestore.collection(Constants.FirestorePaths.QUOTES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val quotes = snapshot?.toObjects(Quote::class.java) ?: emptyList()
                trySend(quotes)
            }
        awaitClose { listener.remove() }
    }

    // ✅ Observe Roadmaps in Real-Time
    fun observeRoadmaps(): Flow<List<RoadmapUI>> = callbackFlow {
        val listener = firestore.collection(Constants.FirestorePaths.ROADMAPS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val roadmaps = snapshot?.documents?.map {
                    RoadmapUI(
                        title = it.getString("title") ?: "",
                        iconResId = getIconResource(it.getString("icon") ?: ""),
                        progressPercent = 0
                    )
                } ?: emptyList()
                trySend(roadmaps)
            }
        awaitClose { listener.remove() }
    }

    // ✅ Get Current User's First Name
    suspend fun getFirstName(): String {
        val uid = auth.currentUser?.uid ?: return "Learner"
        val snapshot = firestore.collection(Constants.FirestorePaths.USERS)
            .document(uid)
            .get()
            .await()
        return snapshot.getString("firstName") ?: "Learner"
    }
}
