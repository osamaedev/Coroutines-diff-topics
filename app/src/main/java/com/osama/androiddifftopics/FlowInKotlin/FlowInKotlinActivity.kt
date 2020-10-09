package com.osama.androiddifftopics.FlowInKotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.osama.androiddifftopics.R
import kotlinx.android.synthetic.main.activity_flow_in_kotlin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlowInKotlinActivity : AppCompatActivity() {

    private lateinit var flow: Flow<Int>

    private lateinit var flow2: Flow<List<String>>

    private lateinit var array: List<String>

    private val TAG = "FlowInKotlinActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow_in_kotlin)
        setupFlow()
        setupSecondFlow()
        setUpClick()
    }

    private fun setupFlow() {
        flow = flow {
            Log.d(TAG, "setupFlow: Starting flow")
            for (i in (0..10)) {
                delay(500)
                Log.d(TAG, "setupFlow: => Emitting $i")
                emit(i)
            }
        }.map {
            it * it
        }.flowOn(Dispatchers.Default)
    }

    private fun setUpClick() {
        launch_flow.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                flow.collect {
                    Log.d(TAG, "setUpClick: => value emitted is => $it")
                }
                flow2.collect {
                    Log.d(
                        TAG,
                        "setUpClick: The second flow value => $it"
                    )    // Here the flow will return a List of Strings
                }
            }
        }
    }

    private fun setupSecondFlow() {
        array = ArrayList()
        array.plus("First element")
        array.plus("The second element")
        array.plus("The 3 element")
        array.plus("The 4 element")
        array.plus("The 5 element")
        array.plus("The 6 element")
        flow2 = flowOf(array.toList())
            .onEach { delay(500) }
            .flowOn(Dispatchers.IO)
    }
}