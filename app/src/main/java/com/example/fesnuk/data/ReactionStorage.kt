package com.example.fesnuk.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

class ReactionStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("user_reactions", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    /**
     * Get user's reactions for a specific post
     * Returns a set of unicode strings that the user has reacted with
     */
    fun getUserReactions(postId: Int): Set<String> {
        Log.d("ReactionStorage", "getUserReactions called for postId: $postId")
        val json = sharedPreferences.getString("post_$postId", null)
        val reactions: Set<String> = if (json != null) {
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson<Set<String>>(json, type) ?: emptySet()
        } else {
            emptySet()
        }
        Log.d("ReactionStorage", "getUserReactions result for postId $postId: $reactions")
        return reactions
    }
    
    /**
     * Add a reaction for the user on a specific post
     */
    fun addUserReaction(postId: Int, unicode: String) {
        Log.d("ReactionStorage", "addUserReaction called - postId: $postId, unicode: $unicode")
        val currentReactions = getUserReactions(postId).toMutableSet()
        val wasAdded = currentReactions.add(unicode)
        if (wasAdded) {
            saveUserReactions(postId, currentReactions)
            Log.d("ReactionStorage", "Successfully added reaction - postId: $postId, unicode: $unicode, new reactions: $currentReactions")
        } else {
            Log.w("ReactionStorage", "Reaction already exists - postId: $postId, unicode: $unicode")
        }
    }
    
    /**
     * Remove a reaction for the user on a specific post
     */
    fun removeUserReaction(postId: Int, unicode: String) {
        Log.d("ReactionStorage", "removeUserReaction called - postId: $postId, unicode: $unicode")
        val currentReactions = getUserReactions(postId).toMutableSet()
        val wasRemoved = currentReactions.remove(unicode)
        if (wasRemoved) {
            saveUserReactions(postId, currentReactions)
            Log.d("ReactionStorage", "Successfully removed reaction - postId: $postId, unicode: $unicode, remaining reactions: $currentReactions")
        } else {
            Log.w("ReactionStorage", "Reaction not found to remove - postId: $postId, unicode: $unicode")
        }
    }
    
    /**
     * Check if user has reacted with a specific emoji on a post
     */
    fun hasUserReacted(postId: Int, unicode: String): Boolean {
        Log.d("ReactionStorage", "hasUserReacted called - postId: $postId, unicode: $unicode")
        val hasReacted = getUserReactions(postId).contains(unicode)
        Log.d("ReactionStorage", "hasUserReacted result - postId: $postId, unicode: $unicode, hasReacted: $hasReacted")
        return hasReacted
    }
    
    /**
     * Toggle a reaction - add if not present, remove if present
     * Returns true if reaction was added, false if removed
     */
    fun toggleReaction(postId: Int, unicode: String): Boolean {
        Log.d("ReactionStorage", "toggleReaction called - postId: $postId, unicode: $unicode")
        return if (hasUserReacted(postId, unicode)) {
            removeUserReaction(postId, unicode)
            Log.d("ReactionStorage", "toggleReaction removed reaction - postId: $postId, unicode: $unicode")
            false
        } else {
            addUserReaction(postId, unicode)
            Log.d("ReactionStorage", "toggleReaction added reaction - postId: $postId, unicode: $unicode")
            true
        }
    }
    
    private fun saveUserReactions(postId: Int, reactions: Set<String>) {
        Log.d("ReactionStorage", "saveUserReactions called - postId: $postId, reactions: $reactions")
        val json = gson.toJson(reactions)
        sharedPreferences.edit()
            .putString("post_$postId", json)
            .apply()
        Log.d("ReactionStorage", "saveUserReactions completed - postId: $postId, saved JSON: $json")
    }
    
    /**
     * Clear all user reactions (for testing or reset purposes)
     */
    fun clearAllReactions() {
        sharedPreferences.edit().clear().apply()
    }
}