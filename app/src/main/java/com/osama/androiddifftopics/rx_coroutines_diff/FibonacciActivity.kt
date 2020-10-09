package com.osama.androiddifftopics.rx_coroutines_diff

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.osama.androiddifftopics.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fibonacci.*
import kotlinx.coroutines.*

class FibonacciActivity : AppCompatActivity() {

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fibonacci)

        calculate.setOnClickListener {
//            if (fibToCalculate.text.toString().isNotEmpty()) {
//                this.executeFibAsync(fibToCalculate.text.toString().toLong())
//            }
            job = this.executeFibAsyncCoroutines(fibToCalculate.text.toString().toLong())
        }


        // In this way se can cancel a running coroutine
        // NOTE: that when cancelling a job I need to reassign
        // it again.
        buttonCancel.setOnClickListener {
            if (job.isActive) {
                job.cancel()
            }
        }
    }


    // The expression to calculate the Fibonacci Of a number
    // is a recursive method.
    private fun fib(n: Long): Long {
        return if (n <= 1) n
        else fib(n - 1) + fib(n - 2)
    }


    // region -- Using RXJava --

    private fun executeFibAsync(number: Long) {
        progressBar.visibility = View.VISIBLE
        fibonacciAsync(number)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progressBar.visibility = View.INVISIBLE
                theResult.text = it.toString()
            }, {
                // we can handel the error here
            })
            .dispose()
    }

    private fun fibonacciAsync(number: Long): Single<Long> =
        Single.create {            // Or we can use the fromCallable method
            val result = fib(number)
            it.onSuccess(result)
        }

    // endregion


    // region -- Using Coroutines Including Cancellation --

    private fun executeFibAsyncCoroutines(number: Long): Job {
        progressBar.visibility = View.VISIBLE
        return CoroutineScope(Dispatchers.Default).launch {
            val result = fib(number)
            // here the executing is done


            // Other propriety that can be used to check if the coroutine is
            // not cancelled is: isActive propriety.

            if (!isActive) {
                // Here do not execute the heavy work anymore
            }

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.INVISIBLE
                theResult.text = result.toString()
            }
        }
    }

    // endregion


    // Cancellation cases

    private val jobToCancel = CoroutineScope(Dispatchers.Default).launch {
        doSomethingHeavy()

        // this method will not let the coroutine to be cancelled ..
        // because it block the thread running on it and it is not suspend fun
        // if in any case I have to use this sleep method, I should check before and
        // after if the coroutines is still active or not

        if (!isActive) return@launch
        Thread.sleep(500)
        if (!isActive) return@launch

        // because the delay method is a suspend fun, so it does not block the thread of the coroutine
        // and if we call the cancel method of the running coroutine, the coroutine will stop executing
        // this whole suspend block
        delay(500)
        doSomethingHeavy()
    }

    private fun cancelJob() = jobToCancel.cancel()


    private fun startFlow() {
        // here the use case is the start the coroutines and cancel it at the same time
        // and see if it will be cancelled or not.
        this.cancelJob()
    }

    private suspend fun doSomethingHeavy() {
        // TODO : a long for loop
    }

}