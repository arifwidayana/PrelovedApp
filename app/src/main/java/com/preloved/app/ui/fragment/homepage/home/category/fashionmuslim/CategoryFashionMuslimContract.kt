package com.preloved.app.ui.fragment.homepage.home.category.fashionmuslim

import androidx.lifecycle.MutableLiveData
import com.preloved.app.base.arch.BaseContract
import com.preloved.app.base.model.Resource
import com.preloved.app.data.network.model.response.category.CategoryResponse

interface CategoryFashionMuslimContract {
    interface View: BaseContract.BaseView {
        fun getCategory()
    }
    interface ViewModel: BaseContract.BaseViewModel {
        fun getDataCategoryResult(): MutableLiveData<Resource<CategoryResponse>>
        fun getDataById(categoryId: Int)
    }
    interface Repository: BaseContract.BaseRepository {
        suspend fun getDataCategoryById(categoryId: Int): CategoryResponse
    }
}