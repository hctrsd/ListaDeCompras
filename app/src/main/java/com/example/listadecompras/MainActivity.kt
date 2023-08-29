package com.example.listadecompras

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.listadecompras.DB.AppDataBase
import com.example.listadecompras.DB.Articulos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     //   lifecyclescope para generar una tarea en segundo plano (LIsta)

        lifecycleScope.launch(Dispatchers.IO) {
            val tareaDao = AppDataBase.getInstance(this@MainActivity).tareaDao()
            val canRegistros = tareaDao.contar()
            if (canRegistros < 1) {
                tareaDao.insertar(Articulos(0, " Pasta", false))

                tareaDao.insertar(Articulos(0, "Pasta Dental", false))
                tareaDao.insertar(Articulos(0, "Detergente Liquido", false))
                tareaDao.insertar(Articulos(0, "Soft 5Lts", false))
            }
        }

//Codigo para el cambio de idioma

val bienvenido = resources.getString(R.string.bienvenido)
        setContent {
            Column{
                Text(bienvenido)
            }

            ListaComprasUI()
        }
    }
}


@Composable
fun ListaComprasUI(){
    val contexto = LocalContext.current
    val (compras, setArticulos) = remember { mutableStateOf(emptyList<Articulos>())}

    LaunchedEffect(compras ){
        withContext(Dispatchers.IO){
            val dao=AppDataBase.getInstance(contexto).tareaDao()
            setArticulos(dao.findAll())
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(compras){tarea ->
            TareaItemUI(tarea){
                setArticulos(emptyList<Articulos>())
            }

        }

        // Agrega elemento
        item {
            CrearTareaUI(contexto = contexto, onTareaCreated = {
                setArticulos(emptyList<Articulos>())
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearTareaUI(contexto: Context, onTareaCreated: () -> Unit) {
    val alcanceCorrutina = rememberCoroutineScope()
    var nuevaTareaText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        TextField(
            value = nuevaTareaText,
            onValueChange = { nuevaTareaText = it },
            modifier = Modifier.weight(2f),
            label = { Text("Nuevo Dato") }
        )

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            onClick = {
                alcanceCorrutina.launch(Dispatchers.IO) {
                    val dao = AppDataBase.getInstance(contexto).tareaDao()
                    val nuevaTarea = Articulos(
                        id = 0,
                        tarea = nuevaTareaText,
                        realizada = false
                    )
                    dao.insertar(nuevaTarea)
                    nuevaTareaText = ""
                    onTareaCreated()
                }
            }
        ) {
            Text("Agregar a Lista")
        }
    }
}


@Composable
fun TareaItemUI(tarea:Articulos, onSave:() ->Unit={}){
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ){
        if(tarea.realizada){
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Tarea realizada",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao= AppDataBase.getInstance(contexto).tareaDao()
                        tarea.realizada=false
                        dao.actualizar(tarea)
                        onSave()
                    }
                }
            )
        }else{
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription="Tarea por hacer",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao= AppDataBase.getInstance(contexto).tareaDao()
                        tarea.realizada=true
                        dao.actualizar(tarea)
                        onSave()
                    }
                }
            )
        }

        Spacer(modifier=Modifier.width(20.dp))
        Text(
            text=tarea.tarea,
            modifier=Modifier.weight(2f)
        )
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar tarea",
            modifier = Modifier.clickable {
                alcanceCorrutina.launch(Dispatchers.IO) {
                    val dao= AppDataBase.getInstance(contexto).tareaDao()
                    dao.eliminar(tarea)
                    onSave()
                }
            }
        )
    }
}


