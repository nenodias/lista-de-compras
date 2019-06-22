package br.com.livrokotlin.listadecompras

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.text.NumberFormat

class ProdutoAdapter(contexto: Context) : ArrayAdapter<Produto>(contexto, 0) {

    var editListener: ((Int, Produto) -> Unit)? = null
    var deleteListener: ((Int, Produto) -> Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v:View
        if(convertView != null){
            v = convertView
        } else {
            //Inflar layout
            v = LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false)
        }
        val item = getItem(position)
        val txt_produto = v.findViewById<TextView>(R.id.txt_item_produto)
        val txt_qtd = v.findViewById<TextView>(R.id.txt_item_qtd)
        val txt_valor = v.findViewById<TextView>(R.id.txt_item_valor)
        val img_produto = v.findViewById<ImageView>(R.id.img_item_foto)
        val icon_edit = v.findViewById<ImageButton>(R.id.icon_edit)
        val icon_delete = v.findViewById<ImageButton>(R.id.icon_delete)

        val f = NumberFormat.getCurrencyInstance()
        txt_produto.text = item.nome
        txt_qtd.text = item.quantidade.toString()
        txt_valor.text = f.format(item.valor)
        if(item.foto != null){
            img_produto.setImageBitmap(item.foto)
        }else{
            img_produto.setImageResource(R.drawable.produto_sem_foto)
        }
        icon_edit.setOnClickListener {
            editListener?.invoke(position, item)
        }
        icon_delete.setOnClickListener {
            deleteListener?.invoke(position, item)
        }
        return v
    }
}