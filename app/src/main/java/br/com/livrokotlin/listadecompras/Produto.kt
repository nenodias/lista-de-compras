package br.com.livrokotlin.listadecompras

import android.graphics.Bitmap
import java.io.Serializable

data class Produto (val id: Int, val nome: String,val quantidade: Int, val valor: Double, val foto: Bitmap? = null) : Serializable