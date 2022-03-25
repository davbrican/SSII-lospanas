from stegano import lsbset
from stegano.lsbset import generators

secret_message = "Hello World!"
secret_image = lsbset.hide("./entrada.jpeg", secret_message, generators.eratosthenes())

secret_image.save("./resultado.bmp")

# Try to decode with another generator
message = lsbset.reveal("./resultado.bmp", generators.eratosthenes())
print(message)