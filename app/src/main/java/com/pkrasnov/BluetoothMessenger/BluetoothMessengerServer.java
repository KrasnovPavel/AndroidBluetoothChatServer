package com.pkrasnov.BluetoothMessenger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static android.content.ContentValues.TAG;

/**
 * Класс, реализующий обмен сообщениями по bluetooth.
 * ВНИМАНИЕ: Использование не-ASCII символов не поддерживается.
 * Пример использования: https://github.com/KrasnovPavel/AndroidBluetoothChatServer
 */
public abstract class BluetoothMessengerServer {
    private final BluetoothServerSocket serverSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private final String name = BluetoothAdapter.getDefaultAdapter().getName();

    private boolean lookingStarted = false;
    private boolean listeningStarted = false;

    /**
     * UUID сервиса Bluetooth, второе устройство должно иметь такой же UUID.
     */
    public final UUID uuid = UUID.fromString("34B1CF4D-1069-4AD6-89B6-E161D79BE4D8");

    /**
     * Конструктор. Выбрасывает иключениме, если возникли проблемы с Bluetooth,
     * например если Bluetooth выключен.
     * @throws IOException
     */
    public BluetoothMessengerServer () throws IOException {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
    }

    /**
     * Функция обрабатывающая полученное сообщение.
     * @param message сообщение
     */
    public abstract void MessageReceived(String message);

    /**
     * Функция обрабатывающая разрыв соединиения с другим утсройством.
     */
    public void ConnectionClosed() {
        StartServer();
    }

    /**
     * Функция запускающая ожидание Bluetooth-клиента.
     */
    public void StartServer() {
        if (lookingStarted) return;

        lookingStarted = true;
        CompletableFuture.runAsync(this::LookingForClient);
    }

    /**
     * Функция отправляющаяя сообщение на подключенное Bluetooth-устройство.
     * @param message сообщение
     */
    public void SendMessage(String message) {
        byte[] length = {(byte)message.length()};
        byte[] str = message.getBytes();
        try {
            outputStream.write(length);
            outputStream.write(str);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция обрабатывающая подключение Bluetooth-устройства.
     * @param socket сокет Bluetooth-устройства
     */
    public void ClientConnected(BluetoothSocket socket) {
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            CompletableFuture.runAsync(this::ListenSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void LookingForClient() {
        while (lookingStarted) {
            try {
                BluetoothSocket socket = serverSocket.accept();
                if (socket != null) {
                    ClientConnected(socket);
                    lookingStarted = false;
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                lookingStarted = false;
            }
        }
    }

    private void ListenSocket() {
        while (true) {
            try {
                byte[] c = new byte[2048];
                int size = inputStream.read(c);
                String s = new String(c, 0, size);
                MessageReceived(s);
            } catch (Exception e) {
                ConnectionClosed();
                return;
            }
        }
    }
}
