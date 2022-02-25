import socket
import threading

def conexion():
    ClientSocket = socket.socket()
    host = '127.0.0.1'
    port = 3030

    print('Waiting for connection')
    try:
        ClientSocket.connect((host, port))
    except socket.error as e:
        print(str(e))

    Response = ClientSocket.recv(1024)
    while True:
        Input = "Hello, world!"
        ClientSocket.send(str.encode(Input))
        Response = ClientSocket.recv(1024)
        print(Response.decode('utf-8'))
        break

    ClientSocket.close()

for i in range(1):
    t = threading.Thread(target=conexion)
    t.start()