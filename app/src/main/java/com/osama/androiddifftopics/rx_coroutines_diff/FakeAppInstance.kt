package com.osama.androiddifftopics.rx_coroutines_diff

import android.app.Application
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.Exception


/**
 *  In this section I am trying to understand  the difference between Coroutines
 *  and RxJava following this series of articles:
 *  https://medium.com/capital-one-tech/coroutines-and-rxjava-an-asynchronicity-comparison-part-1-asynchronous-programming-e726a925342a
 *
 *  The aim is the respond to the question why using Coroutines And not RxJava and what is the
 *  difference between them.
 *
 */
class FakeAppInstance : Application() {


    public fun initializeObjectAsync(): Completable {
        // Here we return an observable that we can observe to
        // using a subscriber
        // this method just show the use case of doing heavy
        // initialization when the app launch
        return Completable.create { emitter ->
            try {
                heavyInitialization()
                if (emitter != null && !emitter.isDisposed) {
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                if (emitter != null && !emitter.isDisposed) {
                    emitter.onError(e)
                }
            }
        }
    }


    fun initializeObjects() {
        initializeObjectAsync()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // here the onComplete is called
                // so the initialization was successful
                // continue our process

            }, {
                // An error occurred !!
                // handle it
            })
    }


    /**
     *  With coroutines the call will be like this
     */
    fun coroutinesInitializeObjects() {
        CoroutineScope(Dispatchers.Default).launch {                        // GlobalScope  is similar to RXJava Schedulers.computation() (I think here is
            // for handling errors
            // we can surround the method this
            // a try catch
            try {
                heavyInitialization()

                // passing to this line means that previous heavy is done
                // don't forgot that this block of code is a suspending lambda
                // so it unblock the thread until the execution is done, and then
                // return this line an continue the work

                // to update a view we have to switch to a
                // context with a Main dispatcher
                withContext(Dispatchers.Main) {
                    // here we can perform UI changes here
                }

            } catch (e: Exception) {
                // handle the exception here
            }
        }
    }


    private fun heavyInitialization() {
        // do heavy work here
    }

}