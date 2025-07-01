# 🍎 iOS Examples - EBSCore SDK

Complete iOS examples demonstrating native iOS development with EBSCore SDK using SwiftUI, Combine, Core Data, and modern iOS architecture patterns.

## 🎯 Overview

These examples show how to build production-ready iOS applications using EBSCore SDK with:
- **SwiftUI** for modern declarative UI development
- **Combine** for reactive programming and data flow
- **Core Data** for local data persistence
- **MVVM Architecture** with ObservableObject ViewModels
- **Offline-First** approach with intelligent sync
- **Background Tasks** for data synchronization
- **Biometric Authentication** with Face ID/Touch ID

## 📱 Setup

### Project Configuration

#### Xcode Project Settings
```swift
// Minimum iOS version: 14.0
// Swift version: 5.9+
// Xcode version: 15.0+
```

#### Package Dependencies (Package.swift)
```swift
// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "EBSCoreIOSExample",
    platforms: [
        .iOS(.v14)
    ],
    products: [
        .library(
            name: "EBSCoreIOSExample",
            targets: ["EBSCoreIOSExample"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/everybytesystems/ebscore-sdk-ios", from: "1.0.0"),
        .package(url: "https://github.com/Alamofire/Alamofire", from: "5.8.0"),
        .package(url: "https://github.com/realm/realm-swift", from: "10.45.0")
    ],
    targets: [
        .target(
            name: "EBSCoreIOSExample",
            dependencies: [
                .product(name: "EBSCoreSDK", package: "ebscore-sdk-ios"),
                "Alamofire",
                .product(name: "RealmSwift", package: "realm-swift")
            ]
        ),
        .testTarget(
            name: "EBSCoreIOSExampleTests",
            dependencies: ["EBSCoreIOSExample"]
        ),
    ]
)
```

#### Info.plist Configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <!-- Face ID Usage -->
    <key>NSFaceIDUsageDescription</key>
    <string>Use Face ID to securely access your health data</string>
    
    <!-- Location Usage -->
    <key>NSLocationWhenInUseUsageDescription</key>
    <string>Location is used to find nearby health facilities</string>
    
    <!-- Camera Usage -->
    <key>NSCameraUsageDescription</key>
    <string>Camera is used to scan QR codes and capture photos</string>
    
    <!-- Background Modes -->
    <key>UIBackgroundModes</key>
    <array>
        <string>background-processing</string>
        <string>background-fetch</string>
    </array>
    
    <!-- App Transport Security -->
    <key>NSAppTransportSecurity</key>
    <dict>
        <key>NSAllowsArbitraryLoads</key>
        <false/>
        <key>NSExceptionDomains</key>
        <dict>
            <key>your-dhis2-instance.org</key>
            <dict>
                <key>NSExceptionAllowsInsecureHTTPLoads</key>
                <true/>
                <key>NSExceptionMinimumTLSVersion</key>
                <string>TLSv1.0</string>
            </dict>
        </dict>
    </dict>
</dict>
</plist>
```

## 🏗️ Architecture Components

### 1. App Entry Point

```swift
// App.swift
import SwiftUI
import EBSCoreSDK

@main
struct EBSCoreApp: App {
    @StateObject private var appState = AppState()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(appState)
                .onAppear {
                    setupEBSCoreSDK()
                    setupBackgroundTasks()
                }
        }
    }
    
    private func setupEBSCoreSDK() {
        let config = EBSCoreConfig(
            baseUrl: "https://play.dhis2.org/2.40.1",
            enableOfflineMode: true,
            enableAnalytics: true,
            logLevel: .debug
        )
        
        EBSCoreSDK.shared.configure(with: config)
    }
    
    private func setupBackgroundTasks() {
        BackgroundTaskManager.shared.registerTasks()
    }
}

// AppState.swift
import SwiftUI
import Combine

class AppState: ObservableObject {
    @Published var isAuthenticated = false
    @Published var currentUser: User?
    @Published var selectedFacility: HealthFacility?
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        // Check for existing authentication
        checkAuthenticationStatus()
    }
    
    private func checkAuthenticationStatus() {
        // Check keychain for stored credentials
        if let token = KeychainManager.shared.getAccessToken() {
            isAuthenticated = true
            loadCurrentUser()
        }
    }
    
    private func loadCurrentUser() {
        // Load user data from SDK
    }
    
    func signOut() {
        KeychainManager.shared.clearCredentials()
        isAuthenticated = false
        currentUser = nil
        selectedFacility = nil
    }
}
```

### 2. Data Models

```swift
// Models/Patient.swift
import Foundation
import EBSCoreSDK

struct Patient: Identifiable, Codable {
    let id: String
    let firstName: String
    let lastName: String
    let dateOfBirth: Date
    let gender: Gender
    let phoneNumber: String?
    let address: Address?
    let facilityId: String
    let lastUpdated: Date
    let syncStatus: SyncStatus
    
    var fullName: String {
        "\(firstName) \(lastName)"
    }
    
    var age: Int {
        Calendar.current.dateComponents([.year], from: dateOfBirth, to: Date()).year ?? 0
    }
}

enum Gender: String, CaseIterable, Codable {
    case male = "male"
    case female = "female"
    case other = "other"
    case unknown = "unknown"
    
    var displayName: String {
        switch self {
        case .male: return "Male"
        case .female: return "Female"
        case .other: return "Other"
        case .unknown: return "Unknown"
        }
    }
    
    var icon: String {
        switch self {
        case .male: return "person.fill"
        case .female: return "person.fill"
        case .other: return "person.fill"
        case .unknown: return "person.fill"
        }
    }
}

struct Address: Codable {
    let street: String?
    let city: String?
    let state: String?
    let postalCode: String?
    let country: String?
    
    var formattedAddress: String {
        [street, city, state, postalCode, country]
            .compactMap { $0 }
            .filter { !$0.isEmpty }
            .joined(separator: ", ")
    }
}

enum SyncStatus: String, Codable {
    case synced = "synced"
    case pending = "pending"
    case failed = "failed"
    
    var color: Color {
        switch self {
        case .synced: return .green
        case .pending: return .orange
        case .failed: return .red
        }
    }
    
    var icon: String {
        switch self {
        case .synced: return "checkmark.circle.fill"
        case .pending: return "clock.fill"
        case .failed: return "exclamationmark.triangle.fill"
        }
    }
}

// Models/HealthFacility.swift
struct HealthFacility: Identifiable, Codable {
    let id: String
    let name: String
    let code: String
    let level: Int
    let coordinates: Coordinates?
    let parentId: String?
    let lastUpdated: Date
}

struct Coordinates: Codable {
    let latitude: Double
    let longitude: Double
}
```

### 3. Core Data Stack

```swift
// CoreData/PersistenceController.swift
import CoreData
import Foundation

class PersistenceController: ObservableObject {
    static let shared = PersistenceController()
    
    lazy var container: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "EBSCoreModel")
        
        container.loadPersistentStores { _, error in
            if let error = error as NSError? {
                fatalError("Core Data error: \(error), \(error.userInfo)")
            }
        }
        
        container.viewContext.automaticallyMergesChangesFromParent = true
        return container
    }()
    
    var context: NSManagedObjectContext {
        container.viewContext
    }
    
    func save() {
        let context = container.viewContext
        
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                print("Save error: \(error)")
            }
        }
    }
}

// CoreData/PatientEntity+CoreDataClass.swift
import CoreData
import Foundation

@objc(PatientEntity)
public class PatientEntity: NSManagedObject {
    
    func toPatient() -> Patient {
        Patient(
            id: id ?? "",
            firstName: firstName ?? "",
            lastName: lastName ?? "",
            dateOfBirth: dateOfBirth ?? Date(),
            gender: Gender(rawValue: gender ?? "") ?? .unknown,
            phoneNumber: phoneNumber,
            address: addressData.flatMap { try? JSONDecoder().decode(Address.self, from: $0) },
            facilityId: facilityId ?? "",
            lastUpdated: lastUpdated ?? Date(),
            syncStatus: SyncStatus(rawValue: syncStatus ?? "") ?? .pending
        )
    }
    
    func update(from patient: Patient) {
        id = patient.id
        firstName = patient.firstName
        lastName = patient.lastName
        dateOfBirth = patient.dateOfBirth
        gender = patient.gender.rawValue
        phoneNumber = patient.phoneNumber
        facilityId = patient.facilityId
        lastUpdated = patient.lastUpdated
        syncStatus = patient.syncStatus.rawValue
        
        if let address = patient.address {
            addressData = try? JSONEncoder().encode(address)
        }
    }
}

// CoreData/PatientEntity+CoreDataProperties.swift
import CoreData
import Foundation

extension PatientEntity {
    @nonobjc public class func fetchRequest() -> NSFetchRequest<PatientEntity> {
        return NSFetchRequest<PatientEntity>(entityName: "PatientEntity")
    }
    
    @NSManaged public var id: String?
    @NSManaged public var firstName: String?
    @NSManaged public var lastName: String?
    @NSManaged public var dateOfBirth: Date?
    @NSManaged public var gender: String?
    @NSManaged public var phoneNumber: String?
    @NSManaged public var addressData: Data?
    @NSManaged public var facilityId: String?
    @NSManaged public var lastUpdated: Date?
    @NSManaged public var syncStatus: String?
}
```

### 4. Repository Pattern

```swift
// Repository/PatientRepository.swift
import Foundation
import Combine
import CoreData
import EBSCoreSDK

protocol PatientRepositoryProtocol {
    func getPatients(for facilityId: String) -> AnyPublisher<[Patient], Error>
    func getPatient(by id: String) -> AnyPublisher<Patient?, Error>
    func savePatient(_ patient: Patient) -> AnyPublisher<Patient, Error>
    func deletePatient(id: String) -> AnyPublisher<Void, Error>
    func searchPatients(in facilityId: String, query: String) -> AnyPublisher<[Patient], Error>
    func syncPatients(for facilityId: String) -> AnyPublisher<Int, Error>
}

class PatientRepository: PatientRepositoryProtocol {
    private let context: NSManagedObjectContext
    private let sdk: EBSCoreSDK
    
    init(context: NSManagedObjectContext = PersistenceController.shared.context,
         sdk: EBSCoreSDK = EBSCoreSDK.shared) {
        self.context = context
        self.sdk = sdk
    }
    
    func getPatients(for facilityId: String) -> AnyPublisher<[Patient], Error> {
        let request: NSFetchRequest<PatientEntity> = PatientEntity.fetchRequest()
        request.predicate = NSPredicate(format: "facilityId == %@", facilityId)
        request.sortDescriptors = [
            NSSortDescriptor(keyPath: \PatientEntity.lastName, ascending: true),
            NSSortDescriptor(keyPath: \PatientEntity.firstName, ascending: true)
        ]
        
        return Future { [weak self] promise in
            guard let self = self else {
                promise(.failure(RepositoryError.contextNotAvailable))
                return
            }
            
            do {
                let entities = try self.context.fetch(request)
                let patients = entities.map { $0.toPatient() }
                promise(.success(patients))
            } catch {
                promise(.failure(error))
            }
        }
        .eraseToAnyPublisher()
    }
    
    func getPatient(by id: String) -> AnyPublisher<Patient?, Error> {
        let request: NSFetchRequest<PatientEntity> = PatientEntity.fetchRequest()
        request.predicate = NSPredicate(format: "id == %@", id)
        request.fetchLimit = 1
        
        return Future { [weak self] promise in
            guard let self = self else {
                promise(.failure(RepositoryError.contextNotAvailable))
                return
            }
            
            do {
                let entities = try self.context.fetch(request)
                let patient = entities.first?.toPatient()
                promise(.success(patient))
            } catch {
                promise(.failure(error))
            }
        }
        .eraseToAnyPublisher()
    }
    
    func savePatient(_ patient: Patient) -> AnyPublisher<Patient, Error> {
        return Future { [weak self] promise in
            guard let self = self else {
                promise(.failure(RepositoryError.contextNotAvailable))
                return
            }
            
            // Save locally first
            let entity = PatientEntity(context: self.context)
            entity.update(from: patient)
            
            do {
                try self.context.save()
                
                // Try to sync to server
                self.syncPatientToServer(patient) { result in
                    switch result {
                    case .success(let syncedPatient):
                        entity.update(from: syncedPatient)
                        try? self.context.save()
                        promise(.success(syncedPatient))
                    case .failure:
                        // Keep local copy even if sync fails
                        promise(.success(patient))
                    }
                }
            } catch {
                promise(.failure(error))
            }
        }
        .eraseToAnyPublisher()
    }
    
    func deletePatient(id: String) -> AnyPublisher<Void, Error> {
        return Future { [weak self] promise in
            guard let self = self else {
                promise(.failure(RepositoryError.contextNotAvailable))
                return
            }
            
            let request: NSFetchRequest<PatientEntity> = PatientEntity.fetchRequest()
            request.predicate = NSPredicate(format: "id == %@", id)
            
            do {
                let entities = try self.context.fetch(request)
                if let entity = entities.first {
                    self.context.delete(entity)
                    try self.context.save()
                    
                    // Try to delete from server if synced
                    if entity.syncStatus == SyncStatus.synced.rawValue {
                        self.deletePatientFromServer(id) { _ in
                            // Continue regardless of server result
                        }
                    }
                }
                promise(.success(()))
            } catch {
                promise(.failure(error))
            }
        }
        .eraseToAnyPublisher()
    }
    
    func searchPatients(in facilityId: String, query: String) -> AnyPublisher<[Patient], Error> {
        let request: NSFetchRequest<PatientEntity> = PatientEntity.fetchRequest()
        
        let facilityPredicate = NSPredicate(format: "facilityId == %@", facilityId)
        let searchPredicate = NSPredicate(format: "firstName CONTAINS[cd] %@ OR lastName CONTAINS[cd] %@", query, query)
        request.predicate = NSCompoundPredicate(andPredicateWithSubpredicates: [facilityPredicate, searchPredicate])
        
        request.sortDescriptors = [
            NSSortDescriptor(keyPath: \PatientEntity.lastName, ascending: true),
            NSSortDescriptor(keyPath: \PatientEntity.firstName, ascending: true)
        ]
        
        return Future { [weak self] promise in
            guard let self = self else {
                promise(.failure(RepositoryError.contextNotAvailable))
                return
            }
            
            do {
                let entities = try self.context.fetch(request)
                let patients = entities.map { $0.toPatient() }
                promise(.success(patients))
            } catch {
                promise(.failure(error))
            }
        }
        .eraseToAnyPublisher()
    }
    
    func syncPatients(for facilityId: String) -> AnyPublisher<Int, Error> {
        return Future { [weak self] promise in
            guard let self = self else {
                promise(.failure(RepositoryError.contextNotAvailable))
                return
            }
            
            // Sync pending local changes to server
            let request: NSFetchRequest<PatientEntity> = PatientEntity.fetchRequest()
            request.predicate = NSPredicate(format: "syncStatus == %@", SyncStatus.pending.rawValue)
            
            do {
                let pendingEntities = try self.context.fetch(request)
                var syncedCount = 0
                let group = DispatchGroup()
                
                for entity in pendingEntities {
                    group.enter()
                    let patient = entity.toPatient()
                    
                    self.syncPatientToServer(patient) { result in
                        switch result {
                        case .success(let syncedPatient):
                            entity.update(from: syncedPatient)
                            syncedCount += 1
                        case .failure:
                            entity.syncStatus = SyncStatus.failed.rawValue
                        }
                        group.leave()
                    }
                }
                
                group.notify(queue: .main) {
                    try? self.context.save()
                    
                    // Fetch latest data from server
                    self.fetchPatientsFromServer(facilityId) { _ in
                        promise(.success(syncedCount))
                    }
                }
            } catch {
                promise(.failure(error))
            }
        }
        .eraseToAnyPublisher()
    }
    
    // MARK: - Private Methods
    
    private func syncPatientToServer(_ patient: Patient, completion: @escaping (Result<Patient, Error>) -> Void) {
        // Implementation would use EBSCore SDK to sync to DHIS2
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            let syncedPatient = Patient(
                id: UUID().uuidString,
                firstName: patient.firstName,
                lastName: patient.lastName,
                dateOfBirth: patient.dateOfBirth,
                gender: patient.gender,
                phoneNumber: patient.phoneNumber,
                address: patient.address,
                facilityId: patient.facilityId,
                lastUpdated: Date(),
                syncStatus: .synced
            )
            completion(.success(syncedPatient))
        }
    }
    
    private func deletePatientFromServer(_ id: String, completion: @escaping (Result<Void, Error>) -> Void) {
        // Implementation would use EBSCore SDK to delete from DHIS2
        DispatchQueue.global().asyncAfter(deadline: .now() + 0.5) {
            completion(.success(()))
        }
    }
    
    private func fetchPatientsFromServer(_ facilityId: String, completion: @escaping (Result<[Patient], Error>) -> Void) {
        // Implementation would use EBSCore SDK to fetch from DHIS2
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            completion(.success([]))
        }
    }
}

enum RepositoryError: Error {
    case contextNotAvailable
    case syncFailed
    case networkError
}
```

### 5. ViewModels with Combine

```swift
// ViewModels/PatientListViewModel.swift
import Foundation
import Combine
import SwiftUI

class PatientListViewModel: ObservableObject {
    @Published var patients: [Patient] = []
    @Published var searchText = ""
    @Published var isLoading = false
    @Published var isRefreshing = false
    @Published var errorMessage: String?
    @Published var syncMessage: String?
    
    private let repository: PatientRepositoryProtocol
    private var cancellables = Set<AnyCancellable>()
    private let facilityId: String
    
    init(facilityId: String, repository: PatientRepositoryProtocol = PatientRepository()) {
        self.facilityId = facilityId
        self.repository = repository
        
        setupSearchBinding()
        loadPatients()
    }
    
    private func setupSearchBinding() {
        $searchText
            .debounce(for: .milliseconds(300), scheduler: RunLoop.main)
            .removeDuplicates()
            .sink { [weak self] searchText in
                self?.performSearch(query: searchText)
            }
            .store(in: &cancellables)
    }
    
    func loadPatients() {
        isLoading = true
        errorMessage = nil
        
        repository.getPatients(for: facilityId)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { [weak self] completion in
                    self?.isLoading = false
                    if case .failure(let error) = completion {
                        self?.errorMessage = error.localizedDescription
                    }
                },
                receiveValue: { [weak self] patients in
                    self?.patients = patients
                }
            )
            .store(in: &cancellables)
    }
    
    private func performSearch(query: String) {
        if query.isEmpty {
            loadPatients()
        } else {
            repository.searchPatients(in: facilityId, query: query)
                .receive(on: DispatchQueue.main)
                .sink(
                    receiveCompletion: { [weak self] completion in
                        if case .failure(let error) = completion {
                            self?.errorMessage = error.localizedDescription
                        }
                    },
                    receiveValue: { [weak self] patients in
                        self?.patients = patients
                    }
                )
                .store(in: &cancellables)
        }
    }
    
    func refreshPatients() {
        isRefreshing = true
        syncMessage = nil
        
        repository.syncPatients(for: facilityId)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { [weak self] completion in
                    self?.isRefreshing = false
                    if case .failure(let error) = completion {
                        self?.errorMessage = error.localizedDescription
                    }
                },
                receiveValue: { [weak self] syncedCount in
                    self?.syncMessage = "Synced \(syncedCount) patients"
                    self?.loadPatients()
                }
            )
            .store(in: &cancellables)
    }
    
    func deletePatient(_ patient: Patient) {
        repository.deletePatient(id: patient.id)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { [weak self] completion in
                    if case .failure(let error) = completion {
                        self?.errorMessage = error.localizedDescription
                    }
                },
                receiveValue: { [weak self] _ in
                    self?.loadPatients()
                }
            )
            .store(in: &cancellables)
    }
    
    func clearError() {
        errorMessage = nil
    }
    
    func clearSyncMessage() {
        syncMessage = nil
    }
}

// ViewModels/PatientDetailViewModel.swift
class PatientDetailViewModel: ObservableObject {
    @Published var patient: Patient?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var isDeleting = false
    @Published var isDeleted = false
    
    private let repository: PatientRepositoryProtocol
    private var cancellables = Set<AnyCancellable>()
    private let patientId: String
    
    init(patientId: String, repository: PatientRepositoryProtocol = PatientRepository()) {
        self.patientId = patientId
        self.repository = repository
        
        loadPatientDetails()
    }
    
    private func loadPatientDetails() {
        isLoading = true
        errorMessage = nil
        
        repository.getPatient(by: patientId)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { [weak self] completion in
                    self?.isLoading = false
                    if case .failure(let error) = completion {
                        self?.errorMessage = error.localizedDescription
                    }
                },
                receiveValue: { [weak self] patient in
                    self?.patient = patient
                }
            )
            .store(in: &cancellables)
    }
    
    func deletePatient() {
        isDeleting = true
        
        repository.deletePatient(id: patientId)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { [weak self] completion in
                    self?.isDeleting = false
                    if case .failure(let error) = completion {
                        self?.errorMessage = error.localizedDescription
                    }
                },
                receiveValue: { [weak self] _ in
                    self?.isDeleted = true
                }
            )
            .store(in: &cancellables)
    }
}
```

### 6. SwiftUI Views

```swift
// Views/PatientListView.swift
import SwiftUI

struct PatientListView: View {
    let facilityId: String
    @StateObject private var viewModel: PatientListViewModel
    @State private var showingAddPatient = false
    @State private var selectedPatient: Patient?
    
    init(facilityId: String) {
        self.facilityId = facilityId
        self._viewModel = StateObject(wrappedValue: PatientListViewModel(facilityId: facilityId))
    }
    
    var body: some View {
        NavigationView {
            VStack {
                // Search Bar
                SearchBar(text: $viewModel.searchText)
                    .padding(.horizontal)
                
                // Content
                if viewModel.isLoading {
                    ProgressView("Loading patients...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if let errorMessage = viewModel.errorMessage {
                    ErrorView(
                        message: errorMessage,
                        onRetry: viewModel.loadPatients
                    )
                } else if viewModel.patients.isEmpty {
                    EmptyStateView(
                        message: viewModel.searchText.isEmpty ? 
                            "No patients found" : 
                            "No patients match your search"
                    )
                } else {
                    List {
                        ForEach(viewModel.patients) { patient in
                            PatientRowView(patient: patient)
                                .onTapGesture {
                                    selectedPatient = patient
                                }
                                .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                                    Button("Delete", role: .destructive) {
                                        viewModel.deletePatient(patient)
                                    }
                                }
                        }
                    }
                    .refreshable {
                        viewModel.refreshPatients()
                    }
                }
            }
            .navigationTitle("Patients")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Add") {
                        showingAddPatient = true
                    }
                }
            }
            .sheet(isPresented: $showingAddPatient) {
                AddPatientView(facilityId: facilityId) {
                    viewModel.loadPatients()
                }
            }
            .sheet(item: $selectedPatient) { patient in
                PatientDetailView(patientId: patient.id)
            }
            .alert("Sync Complete", isPresented: .constant(viewModel.syncMessage != nil)) {
                Button("OK") {
                    viewModel.clearSyncMessage()
                }
            } message: {
                Text(viewModel.syncMessage ?? "")
            }
            .alert("Error", isPresented: .constant(viewModel.errorMessage != nil)) {
                Button("OK") {
                    viewModel.clearError()
                }
            } message: {
                Text(viewModel.errorMessage ?? "")
            }
        }
    }
}

// Views/PatientRowView.swift
struct PatientRowView: View {
    let patient: Patient
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(patient.fullName)
                    .font(.headline)
                
                Text("DOB: \(patient.dateOfBirth, formatter: DateFormatter.shortDate)")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                if let phone = patient.phoneNumber {
                    Text("Phone: \(phone)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 4) {
                Image(systemName: patient.gender.icon)
                    .foregroundColor(.primary)
                
                HStack(spacing: 4) {
                    Image(systemName: patient.syncStatus.icon)
                        .foregroundColor(patient.syncStatus.color)
                        .font(.caption)
                    
                    Text("\(patient.age)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(.vertical, 4)
    }
}

// Views/PatientDetailView.swift
struct PatientDetailView: View {
    let patientId: String
    @StateObject private var viewModel: PatientDetailViewModel
    @Environment(\.dismiss) private var dismiss
    @State private var showingDeleteAlert = false
    
    init(patientId: String) {
        self.patientId = patientId
        self._viewModel = StateObject(wrappedValue: PatientDetailViewModel(patientId: patientId))
    }
    
    var body: some View {
        NavigationView {
            Group {
                if viewModel.isLoading {
                    ProgressView("Loading patient details...")
                } else if let patient = viewModel.patient {
                    PatientDetailContent(patient: patient)
                } else if let errorMessage = viewModel.errorMessage {
                    ErrorView(message: errorMessage, onRetry: {})
                } else {
                    Text("Patient not found")
                        .foregroundColor(.secondary)
                }
            }
            .navigationTitle("Patient Details")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Close") {
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Menu {
                        Button("Edit") {
                            // Navigate to edit view
                        }
                        
                        Button("Delete", role: .destructive) {
                            showingDeleteAlert = true
                        }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                    }
                }
            }
            .alert("Delete Patient", isPresented: $showingDeleteAlert) {
                Button("Cancel", role: .cancel) { }
                Button("Delete", role: .destructive) {
                    viewModel.deletePatient()
                }
            } message: {
                Text("Are you sure you want to delete this patient? This action cannot be undone.")
            }
            .onChange(of: viewModel.isDeleted) { isDeleted in
                if isDeleted {
                    dismiss()
                }
            }
        }
    }
}

struct PatientDetailContent: View {
    let patient: Patient
    
    var body: some View {
        List {
            Section("Personal Information") {
                DetailRow(label: "Name", value: patient.fullName)
                DetailRow(label: "Date of Birth", value: DateFormatter.longDate.string(from: patient.dateOfBirth))
                DetailRow(label: "Age", value: "\(patient.age) years")
                DetailRow(label: "Gender", value: patient.gender.displayName)
                
                if let phone = patient.phoneNumber {
                    DetailRow(label: "Phone", value: phone)
                }
            }
            
            if let address = patient.address {
                Section("Address") {
                    Text(address.formattedAddress)
                        .foregroundColor(.primary)
                }
            }
            
            Section("System Information") {
                DetailRow(label: "Patient ID", value: patient.id)
                DetailRow(label: "Last Updated", value: DateFormatter.dateTime.string(from: patient.lastUpdated))
                
                HStack {
                    Text("Sync Status")
                        .foregroundColor(.secondary)
                    Spacer()
                    HStack(spacing: 4) {
                        Image(systemName: patient.syncStatus.icon)
                            .foregroundColor(patient.syncStatus.color)
                        Text(patient.syncStatus.rawValue.capitalized)
                            .foregroundColor(patient.syncStatus.color)
                    }
                }
            }
        }
    }
}

struct DetailRow: View {
    let label: String
    let value: String
    
    var body: some View {
        HStack {
            Text(label)
                .foregroundColor(.secondary)
            Spacer()
            Text(value)
                .foregroundColor(.primary)
        }
    }
}
```

### 7. Utility Components

```swift
// Views/Components/SearchBar.swift
import SwiftUI

struct SearchBar: View {
    @Binding var text: String
    
    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.secondary)
            
            TextField("Search patients...", text: $text)
                .textFieldStyle(RoundedBorderTextFieldStyle())
            
            if !text.isEmpty {
                Button("Clear") {
                    text = ""
                }
                .foregroundColor(.secondary)
            }
        }
    }
}

// Views/Components/ErrorView.swift
struct ErrorView: View {
    let message: String
    let onRetry: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "exclamationmark.triangle")
                .font(.system(size: 48))
                .foregroundColor(.red)
            
            Text("Error")
                .font(.title2)
                .fontWeight(.semibold)
            
            Text(message)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
            
            Button("Retry", action: onRetry)
                .buttonStyle(.borderedProminent)
        }
        .padding()
    }
}

// Views/Components/EmptyStateView.swift
struct EmptyStateView: View {
    let message: String
    
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "person.slash")
                .font(.system(size: 48))
                .foregroundColor(.secondary)
            
            Text(message)
                .font(.title3)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding()
    }
}

// Extensions/DateFormatter+Extensions.swift
extension DateFormatter {
    static let shortDate: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        return formatter
    }()
    
    static let longDate: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .long
        return formatter
    }()
    
    static let dateTime: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter
    }()
}
```

### 8. Background Tasks and Sync

```swift
// Services/BackgroundTaskManager.swift
import BackgroundTasks
import UIKit

class BackgroundTaskManager {
    static let shared = BackgroundTaskManager()
    
    private let backgroundSyncIdentifier = "com.everybytesystems.ebscore.sync"
    
    private init() {}
    
    func registerTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: backgroundSyncIdentifier,
            using: nil
        ) { task in
            self.handleBackgroundSync(task: task as! BGAppRefreshTask)
        }
    }
    
    func scheduleBackgroundSync() {
        let request = BGAppRefreshTaskRequest(identifier: backgroundSyncIdentifier)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60) // 15 minutes
        
        try? BGTaskScheduler.shared.submit(request)
    }
    
    private func handleBackgroundSync(task: BGAppRefreshTask) {
        scheduleBackgroundSync() // Schedule next sync
        
        let syncOperation = SyncOperation()
        
        task.expirationHandler = {
            syncOperation.cancel()
        }
        
        syncOperation.completionBlock = {
            task.setTaskCompleted(success: !syncOperation.isCancelled)
        }
        
        let queue = OperationQueue()
        queue.addOperation(syncOperation)
    }
}

// Services/SyncOperation.swift
import Foundation

class SyncOperation: Operation {
    private let repository = PatientRepository()
    
    override func main() {
        guard !isCancelled else { return }
        
        let semaphore = DispatchSemaphore(value: 0)
        
        // Sync patients for all facilities
        // This would typically get facility IDs from user preferences
        let facilityIds = ["facility1", "facility2"] // Example
        
        for facilityId in facilityIds {
            guard !isCancelled else { break }
            
            _ = repository.syncPatients(for: facilityId)
                .sink(
                    receiveCompletion: { _ in
                        semaphore.signal()
                    },
                    receiveValue: { _ in }
                )
        }
        
        semaphore.wait()
    }
}
```

### 9. Biometric Authentication

```swift
// Services/BiometricAuthManager.swift
import LocalAuthentication
import Foundation

class BiometricAuthManager: ObservableObject {
    @Published var isAuthenticated = false
    @Published var biometricType: LABiometryType = .none
    
    init() {
        checkBiometricAvailability()
    }
    
    private func checkBiometricAvailability() {
        let context = LAContext()
        var error: NSError?
        
        if context.canEvaluatePolicy(.biometricAuthentication, error: &error) {
            biometricType = context.biometryType
        }
    }
    
    func authenticate(completion: @escaping (Result<Void, Error>) -> Void) {
        let context = LAContext()
        let reason = "Use biometric authentication to access your health data"
        
        context.evaluatePolicy(.biometricAuthentication, localizedReason: reason) { success, error in
            DispatchQueue.main.async {
                if success {
                    self.isAuthenticated = true
                    completion(.success(()))
                } else if let error = error {
                    completion(.failure(error))
                }
            }
        }
    }
    
    var biometricTypeString: String {
        switch biometricType {
        case .faceID:
            return "Face ID"
        case .touchID:
            return "Touch ID"
        case .none:
            return "None"
        @unknown default:
            return "Unknown"
        }
    }
}

// Views/BiometricLoginView.swift
struct BiometricLoginView: View {
    @StateObject private var biometricManager = BiometricAuthManager()
    @State private var showingError = false
    @State private var errorMessage = ""
    
    let onSuccess: () -> Void
    
    var body: some View {
        VStack(spacing: 32) {
            Image(systemName: biometricIcon)
                .font(.system(size: 64))
                .foregroundColor(.blue)
            
            VStack(spacing: 16) {
                Text("Secure Access")
                    .font(.title)
                    .fontWeight(.semibold)
                
                Text("Use \(biometricManager.biometricTypeString) to securely access your health data")
                    .multilineTextAlignment(.center)
                    .foregroundColor(.secondary)
            }
            
            Button("Authenticate") {
                authenticate()
            }
            .buttonStyle(.borderedProminent)
            .controlSize(.large)
        }
        .padding()
        .alert("Authentication Error", isPresented: $showingError) {
            Button("OK") { }
        } message: {
            Text(errorMessage)
        }
    }
    
    private var biometricIcon: String {
        switch biometricManager.biometricType {
        case .faceID:
            return "faceid"
        case .touchID:
            return "touchid"
        default:
            return "lock.shield"
        }
    }
    
    private func authenticate() {
        biometricManager.authenticate { result in
            switch result {
            case .success:
                onSuccess()
            case .failure(let error):
                errorMessage = error.localizedDescription
                showingError = true
            }
        }
    }
}
```

### 10. Keychain Management

```swift
// Services/KeychainManager.swift
import Security
import Foundation

class KeychainManager {
    static let shared = KeychainManager()
    
    private let service = "com.everybytesystems.ebscore"
    
    private init() {}
    
    func save(key: String, data: Data) -> Bool {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key,
            kSecValueData as String: data
        ]
        
        SecItemDelete(query as CFDictionary)
        
        let status = SecItemAdd(query as CFDictionary, nil)
        return status == errSecSuccess
    }
    
    func load(key: String) -> Data? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        
        return status == errSecSuccess ? result as? Data : nil
    }
    
    func delete(key: String) -> Bool {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key
        ]
        
        let status = SecItemDelete(query as CFDictionary)
        return status == errSecSuccess
    }
    
    // Convenience methods for common operations
    func saveAccessToken(_ token: String) -> Bool {
        guard let data = token.data(using: .utf8) else { return false }
        return save(key: "access_token", data: data)
    }
    
    func getAccessToken() -> String? {
        guard let data = load(key: "access_token") else { return nil }
        return String(data: data, encoding: .utf8)
    }
    
    func clearCredentials() {
        _ = delete(key: "access_token")
        _ = delete(key: "refresh_token")
        _ = delete(key: "user_credentials")
    }
}
```

This comprehensive iOS example demonstrates production-ready patterns for building native iOS applications with EBSCore SDK, including modern SwiftUI architecture, Core Data integration, Combine reactive programming, biometric authentication, and background sync capabilities.