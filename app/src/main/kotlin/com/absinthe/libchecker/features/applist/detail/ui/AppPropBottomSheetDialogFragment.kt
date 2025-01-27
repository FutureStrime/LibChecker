package com.absinthe.libchecker.features.applist.detail.ui

import android.content.pm.PackageInfo
import com.absinthe.libchecker.compat.BundleCompat
import com.absinthe.libchecker.features.applist.detail.bean.AppPropItem
import com.absinthe.libchecker.features.applist.detail.ui.view.AppPropsBottomSheetView
import com.absinthe.libchecker.utils.manifest.ApplicationReader
import com.absinthe.libraries.utils.base.BaseBottomSheetViewDialogFragment
import com.absinthe.libraries.utils.view.BottomSheetHeaderView
import java.io.File
import pxb.android.axml.ValueWrapper

const val EXTRA_PACKAGE_INFO = "EXTRA_PACKAGE_INFO"

class AppPropBottomSheetDialogFragment :
  BaseBottomSheetViewDialogFragment<AppPropsBottomSheetView>() {

  private val packageInfo by lazy {
    BundleCompat.getParcelable<PackageInfo>(requireArguments(), EXTRA_PACKAGE_INFO)!!
  }

  override fun initRootView(): AppPropsBottomSheetView =
    AppPropsBottomSheetView(requireContext(), packageInfo)

  override fun getHeaderView(): BottomSheetHeaderView = root.getHeaderView()

  override fun init() {
    root.post {
      maxPeekSize = ((dialog?.window?.decorView?.height ?: 0) * 0.67).toInt()
    }
    val propsMap =
      ApplicationReader.getManifestProperties(File(packageInfo.applicationInfo.sourceDir))
    val bundleList = if (propsMap.isNullOrEmpty()) {
      emptyList()
    } else {
      propsMap.map { prop ->
        AppPropItem(
          key = prop.key,
          value = when (val value = prop.value) {
            is ValueWrapper -> value.ref.toString()
            else -> value?.toString().orEmpty()
          }
        )
      }.sortedBy { item -> item.key }
    }
    root.adapter.setList(bundleList)
  }
}
