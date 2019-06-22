package br.com.livrokotlin.listadecompras

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import  org.jetbrains.anko.db.insert;
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cadastro.*
import org.jetbrains.anko.db.update
import org.jetbrains.anko.toast

class CadastroActivity : AppCompatActivity() {

    val COD_IMAGE: Int = 101

    var imageBitMap: Bitmap? = null

    var idProduto: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val produto = intent?.getSerializableExtra("produto")
        if( produto != null && produto is Produto) {
            idProduto = produto.id
            txt_produto.setText(produto.nome)
            txt_qtd.setText(produto.quantidade.toString())
            txt_valor.setText(produto.valor.toString())
            if(produto.foto != null) {
                img_foto_produto.setImageBitmap(produto.foto)
            }
        } else {
            idProduto = null
        }


        btn_inserir.setOnClickListener {
            val produto = txt_produto.text.toString()
            val qtd = txt_qtd.text.toString()
            val valor = txt_valor.text.toString()
            if(produto.isNotBlank() && qtd.isNotBlank() && valor.isNotBlank()) {
                database.use {
                    var operacao = "inserido"
                    if(idProduto == null) {
                        val idNovo = insert(
                            "produtos",
                            "nome" to produto,
                            "quantidade" to qtd.toInt(),
                            "valor" to valor.toDouble(),
                            "foto" to imageBitMap?.toByteArray()
                        )
                        if (idNovo != -1L) {
                            toast("Item $operacao com sucesso!")
                        } else {
                            toast("Erro ao inserir no banco de dados!")
                        }
                    } else {
                        operacao = "atualizado"
                        update("produtos",
                            "nome" to produto,
                            "quantidade" to qtd.toInt(),
                            "valor" to valor.toDouble(),
                            "foto" to imageBitMap?.toByteArray()).whereArgs(" id = {id}", "id" to idProduto.toString()).exec()
                        toast("Item $operacao com sucesso!")
                        idProduto = null
                    }

                    limparCampos()
                }
                //finish();//Se quiser que quando o item for salvo voltar para a listagem
            } else {
                txt_produto.error = if(txt_produto.text.isEmpty()) "Preencha o nome" else null
                txt_qtd.error = if(txt_qtd.text.isEmpty()) "Preencha a quantidade" else null
                txt_valor.error = if(txt_valor.text.isEmpty()) "Preencha o valor" else null
            }
        }
        img_foto_produto.setOnClickListener {
            abrirGaleria()
        }
    }

    fun limparCampos(){
        idProduto = null
        txt_produto.text.clear()
        txt_qtd.text.clear()
        txt_valor.text.clear()
    }

    fun abrirGaleria(){
        //Definindo a ação de conteúdo
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        //Filtrando os arquivos do tipo imagem
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), COD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == COD_IMAGE && resultCode == Activity.RESULT_OK){
            if(data != null){
                //Passar a imagem escolhida através da variável data
                val inputStream = contentResolver.openInputStream(data.getData());
                imageBitMap = BitmapFactory.decodeStream(inputStream)
                img_foto_produto.setImageBitmap(imageBitMap)
            }
        }
    }
}
