from typing import List, Set, Tuple

class Estado:
    def __init__(self, nombre: str):
        self.nombre = nombre
        self.es_final = False
        self.transiciones = {}

    def __lt__(self, other):
        return self.nombre < other.nombre

    def __eq__(self, other):
        return self.nombre == other.nombre

    def __hash__(self):
        return hash(self.nombre)

    def recorrido_vacio(self) -> Set['Estado']:
        explorado = set([self])
        return self._recorrido_vacio(explorado)

    def _recorrido_vacio(self, explorado: Set['Estado']) -> Set['Estado']:
        estados_nuevos = set()
        for estado_destino in self.transiciones.get('#', []):
            if estado_destino not in explorado:
                explorado.add(estado_destino)
                estados_nuevos.update(estado_destino._recorrido_vacio(explorado))
        return explorado.union(estados_nuevos)

    def leer_palabra_afd(self, palabra: str) -> bool:
        estado_actual = self
        print(f"_{palabra} {estado_actual.nombre}")
        for caracter in palabra:
            siguiente = estado_actual.transiciones.get(caracter, None)
            if siguiente is None:
                print("Rechazado")
                return False
            estado_actual = siguiente
            print(f"{palabra[0:palabra.index(caracter)+1]}_{palabra[palabra.index(caracter)+1:]} {estado_actual.nombre}")
        if estado_actual.es_final:
            print("Aprobado")
            return True
        print("Rechazado")
        return False

    def leer_palabra_afnd(self, palabra: str) -> bool:
        estados_actuales: Set[Estado] = set([self])
        lectura = False
        aprobada = True
        print(f"_{palabra} {', '.join(estado.nombre for estado in estados_actuales)}")
        for i, caracter in enumerate(palabra):
            estados_siguientes = set()
            for estado in estados_actuales:
                estados_siguientes.update(estado.transiciones.get(caracter, []))
            if not estados_siguientes:
                aprobada = False
                break
            print(f"{palabra[:i+1]}_{palabra[i+1:]} {', '.join(estado.nombre for estado in estados_siguientes)}")
            if i == len(palabra) - 1:
                lectura = any(estado.es_final for estado in estados_siguientes)
            else:
                estados_actuales = estados_siguientes
        if lectura and aprobada:
            print("Aprobado")
            return True
        print("Rechazado")
        return False

    def leer_palabra_afnde(self, palabra: str) -> bool:
        estados_actuales = self.recorrido_vacio()
        lectura = False
        aceptada = True
        print(f"_{palabra} {', '.join(estado.nombre for estado in estados_actuales)}")
        for i, caracter in enumerate(palabra):
            estados_siguientes = set()
            for estado in estados_actuales:
                estados_siguientes.update(estado.transiciones.get(caracter, []))
            if not estados_siguientes:
                aceptada = False
                break
            print(f"{palabra[:i+1]}_{palabra[i+1:]} {', '.join(estado.nombre for estado in estados_siguientes)}")
            if i == len(palabra) - 1:
                lectura = any(estado.es_final for estado in estados_siguientes)
            else:
                estados_actuales = set()
                for estado in estados_siguientes:
                    estados_actuales.update(estado.recorrido_vacio())
        if lectura and aceptada:
            print("Aceptado")
            return True
        print("Rechazado")
        return False

    def agregar_transicion(self, entrada: str, destino: 'Estado'):
        self.transiciones.setdefault(entrada, []).append(destino)

def leer_automata() -> Tuple[List[Estado], Estado, str]:
    # Ingrese la lista de nombres de estados
    estados_nombre = input("Ingrese la lista de nombres de estados: ").split()
    for nombre in estados_nombre:
        if nombre in ['"', "'", ",", ".", "_"]:
            print("Error encontrado en la línea 1")
            return [], None, ""

    # Ingrese los caracteres admitidos
    caracteres_admitidos = set(input("Ingrese los caracteres admitidos: "))
    caracteres_admitidos.add('#')
    caracteres_admitidos_copy = caracteres_admitidos.copy()
    for caracter in caracteres_admitidos_copy:
        if not caracter.isalnum() or len(caracter) != 1:
            caracteres_admitidos.remove(caracter)
    if not caracteres_admitidos:
        print("Error encontrado en la línea 2")
        return [], None, ""

    # Ingrese el nombre del estado inicial
    estado_inicial_nombre = input("Ingrese el nombre del estado inicial: ")
    if estado_inicial_nombre not in estados_nombre:
        print("Error encontrado en la línea 3")
        return [], None, ""

    # Ingrese los nombres de los estados finales (separados por un espacio)
    estados_finales_nombres = input("Ingrese los nombres de los estados finales (separados por espacios): ").split()
    for nombre in estados_finales_nombres:
        if nombre not in estados_nombre:
            print("Error encontrado en la línea 4")
            return [], None, ""

    estados: List[Estado] = [Estado(nombre) for nombre in estados_nombre]
    estado_inicial = next((estado for estado in estados if estado.nombre == estado_inicial_nombre), None)
    for estado in estados:
        if estado.nombre in estados_finales_nombres:
            estado.es_final = True

    # Ingrese la lista de transiciones
    transiciones = input("Ingrese la lista de transiciones (separadas por espacios): ").split()
    for transicion in transiciones:
        try:
            origen_nombre, entrada, destino_nombre = transicion.strip('()').split(',')
            origen = next(estado for estado in estados if estado.nombre == origen_nombre[0])
            destino = next(estado for estado in estados if estado.nombre == destino_nombre[0])
            if entrada not in caracteres_admitidos:
                print("Error encontrado en la línea 5")
                return [], None, ""
            origen.agregar_transicion(entrada, destino)
        except Exception:
            print("Error encontrado en la línea 5")
            return [], None, ""

    # Ingrese la cadena de entrada
    cadena_entrada = input("Ingrese la cadena de entrada: ")
    return estados, estado_inicial, cadena_entrada

if __name__ == '__main__':
    estados, estado_inicial, cadena_entrada = leer_automata()
    if estado_inicial:
        estado_inicial.leer_palabra_afnde(cadena_entrada)
