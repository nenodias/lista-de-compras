package br.com.livrokotlin.listadecompras

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val produtosAdapter = ProdutoAdapter(this)
        produtosAdapter.deleteListener = {
            position: Int, item: Produto ->
            //Lambda
            val item = produtosAdapter.getItem(position)
            deletarProduto(item.id)
        }
        produtosAdapter.editListener = { position: Int, item: Produto ->
            editarProduto(item)
        }
        list_view_produtos.adapter = produtosAdapter


        btn_adicionar.setOnClickListener {
            startActivity<CadastroActivity>() // Usando a biblioteca Anko
        }

    }

    fun deletarProduto(idProduto: Int) {
        alert("Deseja excluir o produto?", "Excluir"){
            yesButton {
                database.use {
                    delete("produtos", "id = {id}","id" to idProduto)
                }
                toast("Item deletado com sucesso!")
                updateAdapter()
            }
        }.show()
    }

    fun editarProduto(item: Produto){
        alert("Deseja editar o produto?", "Editar"){
            yesButton {
                //TODO
                startActivity(intentFor<CadastroActivity>().putExtra("produto", item))
            }
        }.show()
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
