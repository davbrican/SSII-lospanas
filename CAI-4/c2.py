from stegano import lsbset
from stegano.lsbset import generators
import os
from chacha20poly1305 import ChaCha20Poly1305

padding = "  "

secret_message = b'Hello World!' + padding.encode()
key = os.urandom(32)
cip = ChaCha20Poly1305(key)

nonce = os.urandom(12)
ciphertext = cip.encrypt(nonce, secret_message)
secret_image = lsbset.hide("./entrada.jpeg", secret_message, generators.eratosthenes())

secret_image.save("./resultado.bmp")

# Try to decode with another generator
message = lsbset.reveal("./resultado.bmp", generators.eratosthenes())
print(message)