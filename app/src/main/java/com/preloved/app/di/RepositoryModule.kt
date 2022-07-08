package com.preloved.app.di

import com.preloved.app.ui.fragment.homepage.account.AccountRepository
import com.preloved.app.ui.fragment.homepage.home.category.all.CategoryAllRepository
import com.preloved.app.ui.fragment.homepage.home.category.food.CategoryFoodRepository
import com.preloved.app.ui.fragment.homepage.home.category.food.CategoryFoodViewModel
import com.preloved.app.ui.fragment.login.LoginRepository
import com.preloved.app.ui.fragment.register.RegisterRepository
import com.preloved.app.ui.fragment.splash.SplashRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::SplashRepository)
    singleOf(::LoginRepository)
    singleOf(::RegisterRepository)
    singleOf(::EditProfileRepository)
    singleOf(::CategoryAllRepository)
    singleOf(::CategoryFoodRepository)
    singleOf(::CategoryElectronicRepository)
    singleOf(::CategoryComputerRepository)
    singleOf(::AccountRepository)
    singleOf(::SellRepository)
    singleOf(::PreviewProductRepository)
    singleOf(::SaleRepository)
}