package org.foundaml.exampleapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    val logger = Logger.getLogger("MainActivityLogger")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-18-216-66-92.us-east-2.compute.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val queryService = retrofit.create(SmartReplyApi::class.java)

        mailTo.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                suggested_answers.visibility = View.GONE
            }
        }

        mailSubject.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                suggested_answers.visibility = View.VISIBLE
            }
        }

        mailBody.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                suggested_answers.visibility = View.VISIBLE
            }
        }

        queryService.postPrediction(
            PostPredictionRequest(
            "smart-reply-example",
                "heuristic-1",
                listOf(
                    listOf(
                        ""
                    )
                )
            )
        ).enqueue(object: Callback<PostPredictionResponse> {
            override fun onResponse(call: Call<PostPredictionResponse>, response: Response<PostPredictionResponse>) {
                if(response.isSuccessful) {
                    response.body()?.let { body ->
                        Logger.getLogger("MainActivityLogger").info(body.labels.toString())
                        val suggestedAnswer1 = body.labels[0]
                        answer1.text = suggestedAnswer1.label
                        answer1.setOnClickListener {
                            queryService.postExample(body.id, suggestedAnswer1.label, true).enqueue(object: Callback<Any> {
                                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                                    if(response.isSuccessful) {
                                        logger.info("Successfully sent example")
                                    } else {
                                        logger.warning("Failed to send example")
                                    }
                                }

                                override fun onFailure(call: Call<Any>, t: Throwable) {
                                    logger.warning("An error occurred: " + t.message)
                                }

                            })
                            when {
                                mailTo.hasFocus() -> {
                                    mailTo.setText(listOf(mailTo.text.toString(), suggestedAnswer1.label).joinToString(separator = " ") { it })
                                    mailTo.setSelection(mailTo.length())
                                }
                                mailSubject.hasFocus() -> {
                                    mailSubject.setText(listOf(mailSubject.text.toString(), suggestedAnswer1.label).joinToString(separator = " ") { it })
                                    mailSubject.setSelection(mailSubject.length())
                                }
                                mailBody.hasFocus() -> {
                                    mailBody.setText(listOf(mailBody.text.toString(), suggestedAnswer1.label).joinToString(separator = " ") { it })
                                    mailBody.setSelection(mailBody.length())
                                }
                            }
                        }

                        val suggestedAnswer2 = body.labels[1]
                        answer2.text = suggestedAnswer2.label
                        answer2.setOnClickListener {
                            when {
                                mailTo.hasFocus() -> {
                                    mailTo.setText(listOf(mailTo.text.toString(), suggestedAnswer1.label).joinToString(separator = " ") { it })
                                    mailTo.setSelection(mailTo.length())
                                }
                                mailSubject.hasFocus() -> {
                                    mailSubject.setText(listOf(mailSubject.text.toString(), suggestedAnswer2.label).joinToString(separator = " ") { it })
                                    mailSubject.setSelection(mailSubject.length())
                                }
                                mailBody.hasFocus() -> {
                                    mailBody.setText(listOf(mailBody.text.toString(), suggestedAnswer2.label).joinToString(separator = " ") { it })
                                    mailBody.setSelection(mailBody.length())
                                }
                            }
                        }
                    }
                } else {
                    logger.warning("response not successful")
                }
            }

            override fun onFailure(call: Call<PostPredictionResponse>, t: Throwable) {
                logger.warning("error: " + t.message)

            }

        })

    }
}
