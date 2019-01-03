package com.example.krasn.BluetoothMessenger;

import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pkrasnov.BluetoothMessenger.BluetoothMessengerServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private class MABluetoothMessenger extends BluetoothMessengerServer {
        MABluetoothMessenger() throws IOException {
            super();
        }

        @Override
        public void MessageReceived(String message) {
            inputTextView.append(message + "|");
        }

        @Override
        public void ConnectionClosed() {
            super.ConnectionClosed();
            inputTextView.append("Connection closed");
        }

        @Override
        public void ClientConnected(BluetoothSocket socket) {
            super.ClientConnected(socket);
            inputTextView.append("Connected|");
        }

        @Override
        public void StartServer() {
            super.StartServer();
            inputTextView.append("Server started|");
        }
    }


    private EditText outputTextEdit;
    private TextView inputTextView;
    BluetoothMessengerServer messenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outputTextEdit = findViewById(R.id.OutputTextEdit);
        inputTextView = findViewById(R.id.InputTextView);

        Button startButton = findViewById(R.id.StartButton);
        Button sendButton = findViewById(R.id.SendButton);

        try {
            messenger = new MABluetoothMessenger();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messenger.StartServer();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messenger.SendMessage(outputTextEdit.getText().toString());
                outputTextEdit.setText("");
            }
        });
    }
}
