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
    estados_nombre = input().split()
    caracteres_admitidos = input().split() + ['#']
    estado_inicial_nombre = input()
    estados_finales_nombres = input().split()

    estados: List[Estado] = [Estado(nombre) for nombre in estados_nombre]
    estado_inicial = next(estado for estado in estados if estado.nombre == estado_inicial_nombre)
    for estado in estados:
        if estado.nombre in estados_finales_nombres:
            estado.es_final = True

    transiciones = input().split()
    for transicion in transiciones:
        origen_nombre, entrada, destino_nombre = transicion.split(',')
        origen = next(estado for estado in estados if estado.nombre == origen_nombre[1:])
        destino = next(estado for estado in estados if estado.nombre == destino_nombre[:-1])
        origen.agregar_transicion(entrada, destino)

    cadena_entrada = input()
    return estados, estado_inicial, cadena_entrada

if __name__ == '__main__':
    estados, estado_inicial, cadena_entrada = leer_automata()
    estado_inicial.leer_palabra_afnde(cadena_entrada)