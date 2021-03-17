package com.example.testapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.BufferedReader
import java.io.DataInput
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var ipText:EditText
    lateinit var messageText:EditText
    lateinit var sendBtn:Button
    lateinit var recMsgText :EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ipText = findViewById(R.id.ipEditText)
        messageText = findViewById(R.id.MessageEditText)
        sendBtn = findViewById(R.id.SendBtn)
        recMsgText= findViewById(R.id.sentMessage)

        sendBtn.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v: View?) {

                        var b : BackGroundTask = BackGroundTask()
                b.execute(ipText.getText().toString(), messageText.getText().toString())
            }

        })

        var thread = Thread(MyServer())
        thread.start()
    }

    inner class MyServer : Runnable
    {

        lateinit var serverSock: ServerSocket
        lateinit var socket: Socket
        lateinit var dis: DataInputStream
        lateinit var message: String
         var handler = Handler()
        override fun run() {

            try {
                serverSock = ServerSocket(9700)
                handler.post(object : Runnable {
                    override fun run() {
                       Toast.makeText(applicationContext, "Waithing for client", Toast.LENGTH_SHORT).show()
                    }

                })
                while(true)
                {
                    socket = serverSock.accept()
                    dis = DataInputStream(socket.getInputStream())
                    message = dis.readUTF()
                    handler.post(object :Runnable {
                        override fun run() {
                            recMsgText.setText(message)
                            Toast.makeText(applicationContext, "Received " + message, Toast.LENGTH_SHORT).show()
                        }
                    })

                }
            }
            catch (e:java.lang.Exception)
            {
                e.printStackTrace()
            }


        }

    }

    inner class BackGroundTask : AsyncTask<String, Void, String>()
    {
        lateinit var socket:Socket
        lateinit var dos : DataOutputStream
        lateinit var ip:String
        lateinit var message:String
        override fun doInBackground(vararg params: String?): String? {

            ip = params[0].toString()
            message = params[1].toString()

            try {
                socket = Socket(ip, 9700)
                dos = DataOutputStream(socket.getOutputStream())
                dos.writeUTF(message)
                dos.close()
                socket.close()
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }

            return null
        }

    }
}