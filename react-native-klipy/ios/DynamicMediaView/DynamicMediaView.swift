//
//  DynamicMediaView.swift
//  KlipyiOS-demo
//
//  Created by Tornike Gomareli on 15.01.25.
//

import SwiftUI
import AlertToast

@available(iOS 17.0, *)
struct DynamicMediaView: View {
  @State private var viewModel = DynamicMediaViewModel()
  @State private var searchText = ""
  @State private var rows: [RowLayout] = []
  
  @FocusState private var isSearchFocused: Bool
  
  let onSend: (GridItemLayout) -> Void
  @Binding var previewItem: GlobalMediaItem?
  @Binding var sheetHeight: SheetHeight
  
  @Environment(\.dismiss) private var dismiss
  var searchDebouncer = SearchDebouncer()
  
  @State private var calculator = MasonryLayoutCalculator()
  
  private let themeColor = Color(red: 44/255, green: 44/255, blue: 46/255)
  
  var body: some View {
    @Bindable var bindableViewModel = viewModel
      
    ZStack {
      themeColor.ignoresSafeArea()
        
      VStack(spacing: 0) {
        // Header: Search Bar
        MediaSearchBar(
          searchText: $searchText,
          selectedCategory: $bindableViewModel.activeCategory,
          isFocused: _isSearchFocused,
          categories: viewModel.categories,
          sheetHeight: $sheetHeight
        )
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(themeColor)
        .zIndex(1)
        
        .onChange(of: viewModel.activeCategory) { _, newCategory in
           guard let category = newCategory else { return }
           viewModel.resetState()
           switch category.type {
           case .trending:
             Task {
               let items = try await viewModel.baseLoadTrendingItems()
               viewModel.items = items
             }
           case .recents:
             Task {
               let items = try await viewModel.baseLoadRecentItems()
               viewModel.items = items
             }
           case .none:
             viewModel.categorySearchText = category.name
           }
        }

        // Content: Grid
        mediaContent
          .layoutPriority(1) // Ensures this takes up all available space
        
        // Footer: Type Selector
        mediaTypeSelector
          .zIndex(1)
      }
    }
    .task {
      if !viewModel.hasCompletedInitialLoad {
        await viewModel.checkServicesHealth()
        await viewModel.fetchCategories()
        await viewModel.initialLoad()
        self.rows = calculator.createRows(from: viewModel.items, withMeta: viewModel.gridMeta)
      }
    }
    .onChange(of: viewModel.shouldDismiss) { _, shouldDismiss in
      if shouldDismiss { dismiss() }
    }
    .onChange(of: searchText) { _, newValue in
      Task { @MainActor in
        await searchDebouncer.debounce {
          if newValue.isEmpty {
            await viewModel.initialLoad()
          } else {
            await viewModel.searchItems(query: newValue)
          }
        }
      }
    }
    .onChange(of: viewModel.categorySearchText) { _, newValue in
      Task { @MainActor in
        if !newValue.isEmpty {
          await viewModel.searchItems(query: newValue)
        }
      }
    }
  }
  
  private var mediaContent: some View {
    ZStack {
      themeColor
      if viewModel.isLoading {
        ProgressView()
          .progressViewStyle(CircularProgressViewStyle(tint: .white))
          .scaleEffect(1.5)
      } else if viewModel.items.isEmpty && viewModel.hasCompletedInitialLoad {
        Text("There is no recent content")
          .foregroundColor(.gray)
      } else {
        MasonryGridView(
          rows: rows,
          hasNext: viewModel.hasMorePages,
          onLoadMore: { Task { await viewModel.loadNextPageIfNeeded() } },
          previewLoaded: { model in
            Task {
              guard let item = viewModel.getMediaItemBySlug(by: model.slug) else { return }
              try await viewModel.trackView(for: item)
            }
          },
          onSend: { mediaItem in
            onSend(mediaItem)
            if let item = viewModel.getMediaItemBySlug(by: mediaItem.slug) {
               Task { try await viewModel.trackShare(for: item) }
            }
          },
          isFocused: _isSearchFocused,
          previewItem: $previewItem,
          selectedCategory: viewModel.activeCategory
        )
        .frame(maxWidth: .infinity, maxHeight: .infinity)
      }
    }
    .frame(maxWidth: .infinity, maxHeight: .infinity)
    .onChange(of: viewModel.items) { _, newValue in
      rows = calculator.createRows(from: newValue, withMeta: viewModel.gridMeta)
    }
  }
  
  private var mediaTypeSelector: some View {
    ZStack {
      themeColor.ignoresSafeArea()
      HStack(spacing: 20) {
        mediaTypeButton("GIFs", type: .gifs)
        mediaTypeButton("Clips", type: .clips)
        mediaTypeButton("Stickers", type: .stickers)
      }
      .padding(.top, 4)
      // No bottom padding so the row can sit flush with the sheet bottom,
      // which is positioned directly on top of the keyboard.
      .padding(.bottom, 0)
    }
    .frame(maxWidth: .infinity)
    .frame(height: 44)
    .background(themeColor) // Ensure opaque background
  }
  
  private func mediaTypeButton(_ title: String, type: MediaType) -> some View {
    let isAvailable = isTypeAvailable(type)
    return Button(action: {
      if isAvailable {
        withAnimation {
          viewModel.switchToType(type)
          calculator = MasonryLayoutCalculator(maxItemsPerRow: type == .clips ? 3 : 4)
          searchText = ""
          Task {
            await viewModel.initialLoad()
            rows = calculator.createRows(from: viewModel.items, withMeta: viewModel.gridMeta)
          }
        }
      }
    }) {
      Text(title)
        .font(.system(size: 17, weight: .bold))
        .foregroundColor(buttonTextColor(for: type, isAvailable: isAvailable))
        .padding(.vertical, 6)
        .padding(.horizontal, 12)
        .background(buttonBackground(for: type, isAvailable: isAvailable))
    }
    .disabled(!isAvailable)
  }
  
  private func buttonTextColor(for type: MediaType, isAvailable: Bool) -> Color {
    if !isAvailable { return .gray }
    return viewModel.currentType == type ? .black : .white
  }
  
  private func buttonBackground(for type: MediaType, isAvailable: Bool) -> some View {
    Group {
      if viewModel.currentType == type && isAvailable {
        RoundedRectangle(cornerRadius: 20).fill(Color(hex: "F8DC3B"))
      } else {
        EmptyView()
      }
    }
  }
  
  private func isTypeAvailable(_ type: MediaType) -> Bool {
    guard let availability = viewModel.mediaAvailability else { return true }
    switch type {
    case .gifs: return availability.gifs.isAlive
    case .clips: return availability.clips.isAlive
    case .stickers: return availability.stickers.isAlive
    case .ad: return true
    }
  }
}