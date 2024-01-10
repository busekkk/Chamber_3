package com.example.chamber_3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class MainActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordEditText2: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<TextView>(R.id.textButton)
        button.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val editText = findViewById<EditText>(R.id.editTextPhone)

        val desiredWidth = (screenWidth * 0.5).toInt()
        val desiredHeight = (screenHeight * 0.2).toInt()

        val params = editText.layoutParams
        params.width = desiredWidth
        params.height = desiredHeight
        editText.layoutParams = params

        val spinner = findViewById<Spinner>(R.id.spinner)
        val items = arrayOf("+90", "+1", "+7")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = items[position]
                // Seçilen öğeyi kullanın
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Hiçbir şey seçilmediğinde yapılacak işlem
            }
        })

        emailEditText = findViewById(R.id.editTextEmailAddress2)
        phoneNumberEditText = findViewById(R.id.editTextPhone)
        passwordEditText = findViewById(R.id.editTextPassword2)
        passwordEditText2 = findViewById(R.id.editTextTextPassword2)

        val submitButton = findViewById<Button>(R.id.button)
        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val password = passwordEditText.text.toString()
            val password2 = passwordEditText2.text.toString()

            if (password == password2) {
                if (email.isNotEmpty() && phoneNumber.isNotEmpty()) {
                    // Verileri API'ye gönder ve token'ı al
                    sendUserDataToAPI(email, phoneNumber, password)
                } else {
                    showToast("Lütfen tüm alanları doldurun.")
                }
            } else {
                showToast("Girilen şifreler eşleşmiyor.")
            }
        }


    }

    private fun sendUserDataToAPI(email: String, phoneNumber: String, password: String) {
        val client = OkHttpClient()

        // JSON verisini oluştur
        val json = """
        {
            "email": "$email",
            "phoneNumber": "$phoneNumber",
            "password": "$password"
        }
    """.trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("http://95.70.151.149:6898/auth/signup")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {

            val responseString = response.body?.string()
            print(responseString)

            //Tokenı SharedPreferences kaydet
            saveTokenToSharedPreferences(responseString)

            //Tokenı kullanarak API ye tekrar bir istek gönder
            sendRequestWithToken(responseString)
            
        } else {
           val errorBody = response.body?.string()
            print("HTTP hatası: ${response.code}, hata mesajı: $errorBody")
            showToast("API'ye veri gönderilirken bir hata oluştu.")

        }
    }

    private fun saveTokenToSharedPreferences(token: String?) {
        val sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.commit()
    }

    private fun sendRequestWithToken(token: String?) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://95.70.151.149:6898/auth/some_endpoint") // Değişmesi gereken yer burası
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseData = response.body?.string()
            // Yanıtı işle

            // Signup işlemi tamamlandıktan sonra MainActivity4'e geçiş yap
            val intent = Intent(this@MainActivity, MainActivity4::class.java)
            startActivity(intent)
        } else {
            showToast("Token ile API'ye istek gönderilirken bir hata oluştu")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
