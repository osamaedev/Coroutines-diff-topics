package com.osama.androiddifftopics.rx_coroutines_diff

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.osama.androiddifftopics.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fibonacci.*

class FibonacciActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fibonacci)

        calculate.setOnClickListener {
            if (fibToCalculate.text.toString().isNotEmpty()) {
                this.executeFibAsync(fibToCalculate.text.toString().toLong())
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

}