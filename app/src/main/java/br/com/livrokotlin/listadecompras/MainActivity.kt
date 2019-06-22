package br.com.livrokotlin.listadecompras

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import java.text.NumberFormat
import java.util.*
import org.jetbrains.anko.startActivity;
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listaProdutos = mutableListOf<String>()
        val produtosAdapter = ProdutoAdapter(this)
        list_view_produtos.adapter = produtosAdapter


        btn_adicionar.setOnClickListener {
//            Exemplo abrindo WebView
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"))

//            val intent = Intent(this, CadastroActivity::class.java)
//            startActivity(intent)
            startActivity<CadastroActivity>() // Usando a biblioteca Anko
        }

        list_view_produtos.setOnItemLongClickListener {
                adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            val item = produtosAdapter.getItem(position)
            deletarProduto(item.id)
            this.updateAdapter()
            true
        }

    }

    fun deletarProduto(idProduto: Int) {
        database.use {
            delete("produtos", "id = {id}","id" to idProduto)
        }
        toast("Item deletado com sucesso!")
    }

    fun updateAdapter() {
        val adapter = list_view_produtos.adapter as ProdutoAdapter
        database.use {
            select("produtos").exec {
                val parser = rowParser {
                    id: Int,
                    nome: String,
                    quantidade: Int,
                    valor: Double,
                    foto: ByteArray? ->
                        Produto(id, nome, quantidade, valor, foto?.toBitmap())
                }
                var listaProdutos = parseList(parser)
                adapter.clear()
                adapter.addAll(listaProdutos)

                val soma = listaProdutos.sumByDouble { it.valor * it.quantidade }
                val f = NumberFormat.getCurrencyInstance(Locale("pt","br"))
                txt_total.text = "Total: ${ f.format(soma) }"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.updateAdapter()
    }
}
