package com.pkrasnov.BluetoothMessenger;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Класс, реализующий обмен сообщениями по bluetooth.
 * ВНИМАНИЕ: Использование не-ASCII символов не поддерживается.
 */
public abstract class BluetoothMessenger {
    private OutputStream outputStream;
    private BluetoothFindThread bluetoothFindThread;

    /**
     * UUID сервиса Bluetooth, второе устройство должно иметь такой же UUID.
     */
    public final UUID uuid = UUID.fromString("34B1CF4D-1069-4AD6-89B6-E161D79BE4D8");

    /**
     * Конструктор. Выбрасывает иключениме, если возникли проблемы с Bluetooth,
     * например если Bluetooth выключен.
     * @throws IOException
     */
    public BluetoothMessenger () throws IOException {
        bluetoothFindThread = new BluetoothFindThread(this, uuid);
    }

    /**
     * Функция обрабатывающая полученное сообщение.
     * @param message сообщение
     */
    public abstract void MessageReceived(String message);

    /**
     * Функция обрабатывающая разрыв соединиения с другим утсройством.
     */
    public abstract void ConnectionClosed();

    /**
     * Функция запускающая сервер Bluetooth-сообщений.
     */
    public void StartServer() {
        if (!bluetoothFindThread.isAlive()) {
            bluetoothFindThread.start();
        }
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
        ListenSocketThread listenSocketThread;
        try {
            outputStream = socket.getOutputStream();
            listenSocketThread = new ListenSocketThread(this, socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        listenSocketThread.start();
    }
}
