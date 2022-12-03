package com.ahmetozaydin.ecommerceapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmetozaydin.ecommerceapp.data.Cart
import com.ahmetozaydin.ecommerceapp.data.CartDatabase
import com.ahmetozaydin.ecommerceapp.data.Favorite
import com.ahmetozaydin.ecommerceapp.data.FavoriteDatabase
import com.ahmetozaydin.ecommerceapp.databinding.EachProductBinding
import com.ahmetozaydin.ecommerceapp.model.Product
import com.ahmetozaydin.ecommerceapp.utils.Utils
import com.ahmetozaydin.ecommerceapp.utils.downloadFromUrl
import com.ahmetozaydin.ecommerceapp.utils.placeholderProgressBar
import com.ahmetozaydin.ecommerceapp.view.ProductDetailsActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProductsAdapter(
    private val products: List<Product>,
    val context: Context,
) : RecyclerView.Adapter<ProductsAdapter.PlaceHolder>() {
    private var favoriteDatabase : FavoriteDatabase? = null
    private var favorite: Favorite? = null
    private var cartDatabase:CartDatabase? = null
    companion object{
         val checkBoxHashmap: HashMap<Int, Boolean> = HashMap<Int, Boolean>()
    }
    interface Listener {
        fun onItemClick(products: Product)//service : Service de alabilir.
    }
    class PlaceHolder(val binding: EachProductBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceHolder {// layout ile bağlama işlemi, view binding ile
        val binding = EachProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaceHolder,
        position: Int
    ) {
        /*Glide.with(context)
            .load(products[position].thumbnail)
            .override(300,300)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.binding.imageOfProduct)*/
            holder.binding.imageOfProduct.downloadFromUrl(products[position].thumbnail,
                placeholderProgressBar(holder.itemView.context))
            holder.binding.textViewProductName.text = products[position].title.toString()
            holder.binding.textViewProductPrice.text = "$" + products[position].price.toString()
        cartDatabase = CartDatabase.invoke(context)
        //activity.runOnUiThread
        //Picasso.with(context).load(products[position].thumbnail).into(holder.binding.imageOfProduct)
        holder.binding.buttonAddToCart.setOnClickListener {
            GlobalScope.launch {
                if(cartDatabase?.cartDao()?.searchForEntity((holder.absoluteAdapterPosition.plus(1))) !=products[position].id
                    || cartDatabase?.cartDao()?.rowCount() == 0) {
                    //INSERT
                    println("INSERT")
                    cartDatabase?.cartDao()?.insertEntity(Cart(
                        products[position].id,
                        products[position].title,
                        products[position].discountPercentage,
                        products[position].description,
                        products[position].price,
                        products[position].rating,
                        products[position].stock,
                        products[position].brand,
                        products[position].thumbnail,
                        0

                    ))
                    println(cartDatabase?.cartDao()?.getAllEntities())
                }else{
                    //DELETE
                    println("DELETE")
                    cartDatabase?.cartDao()?.delete(holder.absoluteAdapterPosition.plus(1))
                    println(cartDatabase?.cartDao()?.getAllEntities())
                }
            }

        }
        
        holder.itemView.setOnClickListener { // holder.binding.cardView.setOnClickListener
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("product", products[position])
            context.startActivity(intent)
            //listener.onItemClick(products[position])
        }
        favoriteDatabase = FavoriteDatabase.invoke(context)
        holder.binding.checkBox.setOnClickListener {
            Utils.vibrateDevice(context)
            GlobalScope.launch{
         /*   if (products[position].id?.let {
                    favoriteDatabase?.favoriteDao()?.searchForEntity(it)
                } == products[position].id) {
                favoriteDatabase?.favoriteDao()?.delete(holder.absoluteAdapterPosition+1)
                println(favoriteDatabase?.favoriteDao()?.getAllEntities())


            }else
            {
                println(favoriteDatabase?.favoriteDao()?.getAllEntities())
                products[position].id?.let { it1 -> insertItem(it1) }
            }*/
                if(favoriteDatabase?.favoriteDao()?.searchForEntity((holder.absoluteAdapterPosition.plus(1))) !=products[position].id
                    || favoriteDatabase?.favoriteDao()?.rowCount() == 0){
                    println("\nINSERT")
                    println("rows count before INSERT : "+favoriteDatabase?.favoriteDao()?.rowCount())
                    favorite = Favorite( //TODO(is there better structure)
                        products[position].id,
                        products[position].title,
                        products[position].description,
                        products[position].price,
                        products[position].rating,
                        products[position].thumbnail,
                        0
                    )
                    favoriteDatabase?.favoriteDao()?.insertEntity(favorite!!)
                    println("rows count after INSERT : "+favoriteDatabase?.favoriteDao()?.rowCount())
                    println("is include the entity : "+favoriteDatabase?.favoriteDao()?.searchForEntity((holder.absoluteAdapterPosition.plus(1)))+" ==, !="+products[position].id)
                    print("getAllEntities : "+favoriteDatabase?.favoriteDao()?.getAllEntities()+"\n")
                }
                else{
                    println("\nDELETE")
                    println("rows count before DELETE : "+favoriteDatabase?.favoriteDao()?.rowCount())
                    println("is include the entity : "+favoriteDatabase?.favoriteDao()?.searchForEntity((holder.absoluteAdapterPosition.plus(1)))+" ==, !="+products[position].id)
                    favoriteDatabase?.favoriteDao()?.delete(holder.absoluteAdapterPosition.plus(1))
                    println("rows count after DELETE : "+favoriteDatabase?.favoriteDao()?.rowCount())
                    println("getAllEntities : "+favoriteDatabase?.favoriteDao()?.getAllEntities()+"\n")
                }
            }



            /*if (holder.binding.checkBox.isChecked)
                checkBoxHashmap.put(holder.absoluteAdapterPosition, true)
            else
                checkBoxHashmap.remove(holder.absoluteAdapterPosition)
            if(!checkBoxHashmap.size.equals(0))
                println(checkBoxHashmap)*/
        }
    }
    override fun getItemCount(): Int {
        return products.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewAttachedToWindow(holder: PlaceHolder) {
        super.onViewAttachedToWindow(holder)
        /*if(checkBoxHashmap.contains(holder.absoluteAdapterPosition)){
            holder.binding.checkBox.isChecked = true
        }*/
        GlobalScope.launch {
            if(favoriteDatabase?.favoriteDao()?.searchForEntity((holder.absoluteAdapterPosition.plus(1))) ==holder.absoluteAdapterPosition.plus(1)){
                holder.binding.checkBox.isChecked = true
            }
        }

    }

    override fun onViewRecycled(holder: PlaceHolder) {
        super.onViewRecycled(holder)

        //holder.binding.checkBox.isChecked = false // - this line do the trick
    }
}