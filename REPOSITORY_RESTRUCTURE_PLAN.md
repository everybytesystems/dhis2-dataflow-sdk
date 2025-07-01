# 🏗️ EBSCore SDK - Complete Repository Restructure Plan

## 🎯 **New Enterprise Architecture**

We're transforming from the current module-based structure to a comprehensive, enterprise-grade architecture that better separates concerns and provides complete health data platform capabilities.

---

## 📊 **Current vs New Structure**

### **Current Structure (Module-Based):**
```
ebscore-sdk/
├── modules/
│   ├── core/
│   ├── auth/
│   ├── network/
│   ├── storage/
│   ├── dhis2/
│   └── ...
├── demos/
├── examples/
└── docs/
```

### **New Structure (Domain-Based):**
```
ebscore-sdk/
├── core/                        # Core utilities and shared logic
├── data/                        # Data modeling and transformation
├── integrations/               # External system adapters
├── sync/                        # Offline sync engine
├── forms/                       # Form engine and renderer
├── analytics/                   # KPI, reports, visualization
├── identity/                    # RBAC, user profiles, multi-tenancy
├── billing/                     # Subscription and billing logic
├── telemetry/                   # Logging, metrics, analytics
├── cli/                         # Command-line tools
├── docs/                        # Documentation
├── examples/                    # Usage examples & demos
└── tests/                       # Unit and integration tests
```

---

## 🚀 **Migration Strategy**

### **Phase 1: Create New Structure (Week 1)**
1. Create new directory structure
2. Move existing code to appropriate domains
3. Update build configuration
4. Preserve git history where possible

### **Phase 2: Enhance and Implement (Weeks 2-4)**
1. Complete missing implementations
2. Add new modules (billing, forms, etc.)
3. Update documentation
4. Create comprehensive examples

### **Phase 3: Testing and Polish (Week 5)**
1. Comprehensive testing
2. Documentation updates
3. Demo applications
4. Performance optimization

---

## 📁 **Detailed New Structure**

### **🔧 core/** - Core utilities and shared logic
```
core/
├── api/                     # Base API client, HTTP handling
│   ├── client/              # HTTP client implementation
│   ├── interceptors/        # Request/response interceptors
│   └── serialization/       # JSON/XML serialization
├── auth/                    # Auth mechanisms: JWT, OAuth2, API keys
│   ├── providers/           # Auth provider implementations
│   ├── tokens/              # Token management
│   └── storage/             # Secure credential storage
├── cache/                   # Caching logic (local/remote)
│   ├── strategies/          # Cache strategies (LRU, TTL, etc.)
│   ├── storage/             # Cache storage backends
│   └── policies/            # Cache policies and invalidation
├── config/                  # Config loading, environment handling
│   ├── loaders/             # Configuration loaders
│   ├── validators/          # Config validation
│   └── environments/        # Environment-specific configs
├── error/                   # Centralized error and exception handling
│   ├── types/               # Error type definitions
│   ├── handlers/            # Error handlers
│   └── recovery/            # Error recovery strategies
└── utils/                   # Shared utilities, time, files, formatters
    ├── time/                # Date/time utilities
    ├── files/               # File operations
    ├── formatters/          # Data formatters
    └── validators/          # Input validation
```

### **📊 data/** - Data modeling and transformation
```
data/
├── models/                  # Domain models (Program, Form, Report, etc.)
│   ├── health/              # Health-specific models
│   ├── forms/               # Form models
│   ├── users/               # User models
│   └── organizations/       # Organization models
├── schema/                  # JSON Schema definitions
│   ├── dhis2/               # DHIS2 schemas
│   ├── fhir/                # FHIR schemas
│   └── custom/              # Custom schemas
├── mapper/                  # Data mappers / transformers
│   ├── dhis2/               # DHIS2 mappers
│   ├── fhir/                # FHIR mappers
│   └── generic/             # Generic mappers
└── validators/              # Schema validation, constraints
    ├── rules/               # Validation rules
    ├── engines/             # Validation engines
    └── reports/             # Validation reports
```

### **🔌 integrations/** - External system adapters
```
integrations/
├── dhis2/                   # DHIS2-specific services
│   ├── api/                 # DHIS2 API clients
│   ├── models/              # DHIS2 data models
│   ├── sync/                # DHIS2 sync logic
│   └── auth/                # DHIS2 authentication
├── fhir/                    # FHIR API support
│   ├── client/              # FHIR client
│   ├── resources/           # FHIR resources
│   └── converters/          # FHIR converters
├── openmrs/                 # OpenMRS integration
│   ├── api/                 # OpenMRS API
│   ├── models/              # OpenMRS models
│   └── sync/                # OpenMRS sync
└── redcap/                  # REDCap / KoboToolbox import
    ├── importers/           # Data importers
    ├── exporters/           # Data exporters
    └── transformers/        # Data transformers
```

### **🔄 sync/** - Offline sync engine
```
sync/
├── jobs/                    # Scheduled or batch sync jobs
│   ├── schedulers/          # Job schedulers
│   ├── queues/              # Job queues
│   └── workers/             # Job workers
├── engine/                  # Sync logic, conflict resolution
│   ├── strategies/          # Sync strategies
│   ├── conflicts/           # Conflict resolution
│   └── policies/            # Sync policies
└── tracker/                 # Entity tracker & delta sync
    ├── changes/             # Change tracking
    ├── deltas/              # Delta calculation
    └── history/             # Change history
```

### **📝 forms/** - Form engine and renderer
```
forms/
├── builder/                 # Form schema builder
│   ├── dsl/                 # Form DSL
│   ├── templates/           # Form templates
│   └── generators/          # Form generators
├── fields/                  # Field types (input, radio, QR, etc.)
│   ├── input/               # Input fields
│   ├── selection/           # Selection fields
│   ├── media/               # Media fields
│   └── custom/              # Custom fields
├── logic/                   # Skip logic, dynamic conditions
│   ├── rules/               # Logic rules
│   ├── conditions/          # Conditions
│   └── actions/             # Actions
└── renderer/                # Output formatters or frontend form bridges
    ├── html/                # HTML renderer
    ├── json/                # JSON renderer
    └── native/              # Native renderer
```

### **📈 analytics/** - KPI, reports, visualization
```
analytics/
├── builder/                 # Report/KPI generator
│   ├── queries/             # Query builders
│   ├── aggregators/         # Data aggregators
│   └── calculators/         # KPI calculators
├── visualization/           # Charts (bar, pie, line)
│   ├── charts/              # Chart implementations
│   ├── maps/                # Map visualizations
│   └── tables/              # Table visualizations
└── exporter/                # PDF/Excel/CSV export logic
    ├── pdf/                 # PDF exporters
    ├── excel/               # Excel exporters
    └── csv/                 # CSV exporters
```

### **👤 identity/** - RBAC, user profiles, multi-tenancy
```
identity/
├── users/                   # User registration/login
│   ├── registration/        # User registration
│   ├── authentication/      # User authentication
│   └── profiles/            # User profiles
├── orgs/                    # Tenant / organization-level access
│   ├── management/          # Organization management
│   ├── hierarchy/           # Organization hierarchy
│   └── settings/            # Organization settings
├── roles/                   # Role-based access
│   ├── definitions/         # Role definitions
│   ├── permissions/         # Permission management
│   └── assignments/         # Role assignments
└── subscriptions/           # User/org to plan linking
    ├── plans/               # Subscription plans
    ├── billing/             # Billing integration
    └── usage/               # Usage tracking
```

### **💰 billing/** - Subscription and billing logic
```
billing/
├── plans/                   # Subscription plans (Free, Pro, etc.)
│   ├── definitions/         # Plan definitions
│   ├── features/            # Feature management
│   └── limits/              # Usage limits
├── payments/                # Payment gateway integration
│   ├── stripe/              # Stripe integration
│   ├── paypal/              # PayPal integration
│   └── processors/          # Payment processors
├── invoices/                # Invoice generation and management
│   ├── generation/          # Invoice generation
│   ├── templates/           # Invoice templates
│   └── delivery/            # Invoice delivery
├── subscriptions/           # Active subscription logic
│   ├── lifecycle/           # Subscription lifecycle
│   ├── upgrades/            # Plan upgrades/downgrades
│   └── cancellations/       # Cancellation handling
└── discounts/               # Promo codes, vouchers, discounts
    ├── codes/               # Promo codes
    ├── vouchers/            # Vouchers
    └── campaigns/           # Discount campaigns
```

### **📊 telemetry/** - Logging, metrics, analytics
```
telemetry/
├── logger/                  # Structured logging
│   ├── formatters/          # Log formatters
│   ├── appenders/           # Log appenders
│   └── filters/             # Log filters
├── metrics/                 # Performance tracking
│   ├── collectors/          # Metric collectors
│   ├── aggregators/         # Metric aggregators
│   └── exporters/           # Metric exporters
└── events/                  # Usage events, SDK telemetry
    ├── tracking/            # Event tracking
    ├── analytics/           # Event analytics
    └── reporting/           # Event reporting
```

### **⚡ cli/** - Command-line tools
```
cli/
├── generate/                # Generate config, forms, etc.
│   ├── config/              # Config generators
│   ├── forms/               # Form generators
│   └── schemas/             # Schema generators
├── sync/                    # Manually trigger syncs
│   ├── commands/            # Sync commands
│   ├── monitors/            # Sync monitoring
│   └── reports/             # Sync reports
└── billing/                 # CLI tools for billing
    ├── plans/               # Plan management
    ├── subscriptions/       # Subscription management
    └── invoices/            # Invoice management
```

---

## 🔄 **Migration Mapping**

### **Current → New Location:**

| Current Module | New Location | Notes |
|----------------|--------------|-------|
| `modules/core/` | `core/api/`, `core/utils/` | Split into API and utilities |
| `modules/network/` | `core/api/client/` | Move to core API |
| `modules/auth/` | `core/auth/` | Keep in core |
| `modules/storage/` | `core/cache/` | Rename to cache |
| `modules/utils/` | `core/utils/` | Keep in core |
| `modules/dhis2/` | `integrations/dhis2/` | Move to integrations |
| `modules/dhis2-*` | `integrations/dhis2/*` | Reorganize under DHIS2 |
| `modules/sync/` | `sync/engine/` | Move to sync domain |
| `modules/ui/` | `forms/renderer/` | Move to forms |
| `modules/sdk/` | `core/api/` | Merge with core API |

### **New Modules to Create:**
- `data/` - Data modeling (NEW)
- `forms/` - Form engine (NEW)
- `analytics/` - Analytics engine (NEW)
- `identity/` - Identity management (NEW)
- `billing/` - Billing system (NEW)
- `telemetry/` - Telemetry system (NEW)
- `cli/` - CLI tools (NEW)
- `integrations/fhir/` - FHIR integration (NEW)
- `integrations/openmrs/` - OpenMRS integration (NEW)
- `integrations/redcap/` - REDCap integration (NEW)

---

## 🎯 **Implementation Priority**

### **Phase 1: Core Migration (Week 1)**
1. ✅ Create new directory structure
2. ✅ Move existing code to new locations
3. ✅ Update build configuration
4. ✅ Update imports and references

### **Phase 2: Essential New Modules (Week 2)**
1. 🆕 `data/models/` - Core data models
2. 🆕 `identity/users/` - User management
3. 🆕 `forms/builder/` - Form builder
4. 🆕 `analytics/builder/` - Analytics builder

### **Phase 3: Advanced Features (Week 3)**
1. 🆕 `billing/plans/` - Subscription plans
2. 🆕 `telemetry/logger/` - Logging system
3. 🆕 `cli/generate/` - CLI tools
4. 🆕 `integrations/fhir/` - FHIR support

### **Phase 4: Integration & Testing (Week 4)**
1. 🔗 Integration testing
2. 📚 Documentation updates
3. 🚀 Demo applications
4. 🎯 Performance optimization

---

## 📋 **Next Steps**

1. **Approve the new structure** ✅
2. **Start migration process** 🚀
3. **Create implementation plan** 📋
4. **Begin development** 💻

Would you like me to start the migration process? I can begin by:
1. Creating the new directory structure
2. Moving existing code to appropriate locations
3. Updating build configurations
4. Creating placeholder files for new modules

---

*This restructure will transform EBSCore SDK into a comprehensive, enterprise-grade health data platform! 🚀*# 🏗️ EBSCore SDK - Complete Repository Restructure Plan

## 🎯 **New Enterprise Architecture**

We're transforming from the current module-based structure to a comprehensive, enterprise-grade architecture that better separates concerns and provides complete health data platform capabilities.

---

## 📊 **Current vs New Structure**

### **Current Structure (Module-Based):**
```
ebscore-sdk/
├── modules/
│   ├── core/
│   ├── auth/
│   ├── network/
│   ├── storage/
│   ├── dhis2/
│   └── ...
├── demos/
├── examples/
└── docs/
```

### **New Structure (Domain-Based):**
```
ebscore-sdk/
├── core/                        # Core utilities and shared logic
├── data/                        # Data modeling and transformation
├── integrations/               # External system adapters
├── sync/                        # Offline sync engine
├── forms/                       # Form engine and renderer
├── analytics/                   # KPI, reports, visualization
├── identity/                    # RBAC, user profiles, multi-tenancy
├── billing/                     # Subscription and billing logic
├── telemetry/                   # Logging, metrics, analytics
├── cli/                         # Command-line tools
├── docs/                        # Documentation
├── examples/                    # Usage examples & demos
└── tests/                       # Unit and integration tests
```

---

## 🚀 **Migration Strategy**

### **Phase 1: Create New Structure (Week 1)**
1. Create new directory structure
2. Move existing code to appropriate domains
3. Update build configuration
4. Preserve git history where possible

### **Phase 2: Enhance and Implement (Weeks 2-4)**
1. Complete missing implementations
2. Add new modules (billing, forms, etc.)
3. Update documentation
4. Create comprehensive examples

### **Phase 3: Testing and Polish (Week 5)**
1. Comprehensive testing
2. Documentation updates
3. Demo applications
4. Performance optimization

---

## 📁 **Detailed New Structure**

### **🔧 core/** - Core utilities and shared logic
```
core/
├── api/                     # Base API client, HTTP handling
│   ├── client/              # HTTP client implementation
│   ├── interceptors/        # Request/response interceptors
│   └── serialization/       # JSON/XML serialization
├── auth/                    # Auth mechanisms: JWT, OAuth2, API keys
│   ├── providers/           # Auth provider implementations
│   ├── tokens/              # Token management
│   └── storage/             # Secure credential storage
├── cache/                   # Caching logic (local/remote)
│   ├── strategies/          # Cache strategies (LRU, TTL, etc.)
│   ├── storage/             # Cache storage backends
│   └── policies/            # Cache policies and invalidation
├── config/                  # Config loading, environment handling
│   ├── loaders/             # Configuration loaders
│   ├── validators/          # Config validation
│   └── environments/        # Environment-specific configs
├── error/                   # Centralized error and exception handling
│   ├── types/               # Error type definitions
│   ├── handlers/            # Error handlers
│   └── recovery/            # Error recovery strategies
└── utils/                   # Shared utilities, time, files, formatters
    ├── time/                # Date/time utilities
    ├── files/               # File operations
    ├── formatters/          # Data formatters
    └── validators/          # Input validation
```

### **📊 data/** - Data modeling and transformation
```
data/
├── models/                  # Domain models (Program, Form, Report, etc.)
│   ├── health/              # Health-specific models
│   ├── forms/               # Form models
│   ├── users/               # User models
│   └── organizations/       # Organization models
├── schema/                  # JSON Schema definitions
│   ├── dhis2/               # DHIS2 schemas
│   ├── fhir/                # FHIR schemas
│   └── custom/              # Custom schemas
├── mapper/                  # Data mappers / transformers
│   ├── dhis2/               # DHIS2 mappers
│   ├── fhir/                # FHIR mappers
│   └── generic/             # Generic mappers
└── validators/              # Schema validation, constraints
    ├── rules/               # Validation rules
    ├── engines/             # Validation engines
    └── reports/             # Validation reports
```

### **🔌 integrations/** - External system adapters
```
integrations/
├── dhis2/                   # DHIS2-specific services
│   ├── api/                 # DHIS2 API clients
│   ├── models/              # DHIS2 data models
│   ├── sync/                # DHIS2 sync logic
│   └── auth/                # DHIS2 authentication
├── fhir/                    # FHIR API support
│   ├── client/              # FHIR client
│   ├── resources/           # FHIR resources
│   └── converters/          # FHIR converters
├── openmrs/                 # OpenMRS integration
│   ├── api/                 # OpenMRS API
│   ├── models/              # OpenMRS models
│   └── sync/                # OpenMRS sync
└── redcap/                  # REDCap / KoboToolbox import
    ├── importers/           # Data importers
    ├── exporters/           # Data exporters
    └── transformers/        # Data transformers
```

### **🔄 sync/** - Offline sync engine
```
sync/
├── jobs/                    # Scheduled or batch sync jobs
│   ├── schedulers/          # Job schedulers
│   ├── queues/              # Job queues
│   └── workers/             # Job workers
├── engine/                  # Sync logic, conflict resolution
│   ├── strategies/          # Sync strategies
│   ├── conflicts/           # Conflict resolution
│   └── policies/            # Sync policies
└── tracker/                 # Entity tracker & delta sync
    ├── changes/             # Change tracking
    ├── deltas/              # Delta calculation
    └── history/             # Change history
```

### **📝 forms/** - Form engine and renderer
```
forms/
├── builder/                 # Form schema builder
│   ├── dsl/                 # Form DSL
│   ├── templates/           # Form templates
│   └── generators/          # Form generators
├── fields/                  # Field types (input, radio, QR, etc.)
│   ├── input/               # Input fields
│   ├── selection/           # Selection fields
│   ├── media/               # Media fields
│   └── custom/              # Custom fields
├── logic/                   # Skip logic, dynamic conditions
│   ├── rules/               # Logic rules
│   ├── conditions/          # Conditions
│   └── actions/             # Actions
└── renderer/                # Output formatters or frontend form bridges
    ├── html/                # HTML renderer
    ├── json/                # JSON renderer
    └── native/              # Native renderer
```

### **📈 analytics/** - KPI, reports, visualization
```
analytics/
├── builder/                 # Report/KPI generator
│   ├── queries/             # Query builders
│   ├── aggregators/         # Data aggregators
│   └── calculators/         # KPI calculators
├── visualization/           # Charts (bar, pie, line)
│   ├── charts/              # Chart implementations
│   ├── maps/                # Map visualizations
│   └── tables/              # Table visualizations
└── exporter/                # PDF/Excel/CSV export logic
    ├── pdf/                 # PDF exporters
    ├── excel/               # Excel exporters
    └── csv/                 # CSV exporters
```

### **👤 identity/** - RBAC, user profiles, multi-tenancy
```
identity/
├── users/                   # User registration/login
│   ├── registration/        # User registration
│   ├── authentication/      # User authentication
│   └── profiles/            # User profiles
├── orgs/                    # Tenant / organization-level access
│   ├── management/          # Organization management
│   ├── hierarchy/           # Organization hierarchy
│   └── settings/            # Organization settings
├── roles/                   # Role-based access
│   ├── definitions/         # Role definitions
│   ├── permissions/         # Permission management
│   └── assignments/         # Role assignments
└── subscriptions/           # User/org to plan linking
    ├── plans/               # Subscription plans
    ├── billing/             # Billing integration
    └── usage/               # Usage tracking
```

### **💰 billing/** - Subscription and billing logic
```
billing/
├── plans/                   # Subscription plans (Free, Pro, etc.)
│   ├── definitions/         # Plan definitions
│   ├── features/            # Feature management
│   └── limits/              # Usage limits
├── payments/                # Payment gateway integration
│   ├── stripe/              # Stripe integration
│   ├── paypal/              # PayPal integration
│   └── processors/          # Payment processors
├── invoices/                # Invoice generation and management
│   ├── generation/          # Invoice generation
│   ├── templates/           # Invoice templates
│   └── delivery/            # Invoice delivery
├── subscriptions/           # Active subscription logic
│   ├── lifecycle/           # Subscription lifecycle
│   ├── upgrades/            # Plan upgrades/downgrades
│   └── cancellations/       # Cancellation handling
└── discounts/               # Promo codes, vouchers, discounts
    ├── codes/               # Promo codes
    ├── vouchers/            # Vouchers
    └── campaigns/           # Discount campaigns
```

### **📊 telemetry/** - Logging, metrics, analytics
```
telemetry/
├── logger/                  # Structured logging
│   ├── formatters/          # Log formatters
│   ├── appenders/           # Log appenders
│   └── filters/             # Log filters
├── metrics/                 # Performance tracking
│   ├── collectors/          # Metric collectors
│   ├── aggregators/         # Metric aggregators
│   └── exporters/           # Metric exporters
└── events/                  # Usage events, SDK telemetry
    ├── tracking/            # Event tracking
    ├── analytics/           # Event analytics
    └── reporting/           # Event reporting
```

### **⚡ cli/** - Command-line tools
```
cli/
├── generate/                # Generate config, forms, etc.
│   ├── config/              # Config generators
│   ├── forms/               # Form generators
│   └── schemas/             # Schema generators
├── sync/                    # Manually trigger syncs
│   ├── commands/            # Sync commands
│   ├── monitors/            # Sync monitoring
│   └── reports/             # Sync reports
└── billing/                 # CLI tools for billing
    ├── plans/               # Plan management
    ├── subscriptions/       # Subscription management
    └── invoices/            # Invoice management
```

---

## 🔄 **Migration Mapping**

### **Current → New Location:**

| Current Module | New Location | Notes |
|----------------|--------------|-------|
| `modules/core/` | `core/api/`, `core/utils/` | Split into API and utilities |
| `modules/network/` | `core/api/client/` | Move to core API |
| `modules/auth/` | `core/auth/` | Keep in core |
| `modules/storage/` | `core/cache/` | Rename to cache |
| `modules/utils/` | `core/utils/` | Keep in core |
| `modules/dhis2/` | `integrations/dhis2/` | Move to integrations |
| `modules/dhis2-*` | `integrations/dhis2/*` | Reorganize under DHIS2 |
| `modules/sync/` | `sync/engine/` | Move to sync domain |
| `modules/ui/` | `forms/renderer/` | Move to forms |
| `modules/sdk/` | `core/api/` | Merge with core API |

### **New Modules to Create:**
- `data/` - Data modeling (NEW)
- `forms/` - Form engine (NEW)
- `analytics/` - Analytics engine (NEW)
- `identity/` - Identity management (NEW)
- `billing/` - Billing system (NEW)
- `telemetry/` - Telemetry system (NEW)
- `cli/` - CLI tools (NEW)
- `integrations/fhir/` - FHIR integration (NEW)
- `integrations/openmrs/` - OpenMRS integration (NEW)
- `integrations/redcap/` - REDCap integration (NEW)

---

## 🎯 **Implementation Priority**

### **Phase 1: Core Migration (Week 1)**
1. ✅ Create new directory structure
2. ✅ Move existing code to new locations
3. ✅ Update build configuration
4. ✅ Update imports and references

### **Phase 2: Essential New Modules (Week 2)**
1. 🆕 `data/models/` - Core data models
2. 🆕 `identity/users/` - User management
3. 🆕 `forms/builder/` - Form builder
4. 🆕 `analytics/builder/` - Analytics builder

### **Phase 3: Advanced Features (Week 3)**
1. 🆕 `billing/plans/` - Subscription plans
2. 🆕 `telemetry/logger/` - Logging system
3. 🆕 `cli/generate/` - CLI tools
4. 🆕 `integrations/fhir/` - FHIR support

### **Phase 4: Integration & Testing (Week 4)**
1. 🔗 Integration testing
2. 📚 Documentation updates
3. 🚀 Demo applications
4. 🎯 Performance optimization

---

## 📋 **Next Steps**

1. **Approve the new structure** ✅
2. **Start migration process** 🚀
3. **Create implementation plan** 📋
4. **Begin development** 💻

Would you like me to start the migration process? I can begin by:
1. Creating the new directory structure
2. Moving existing code to appropriate locations
3. Updating build configurations
4. Creating placeholder files for new modules

---

*This restructure will transform EBSCore SDK into a comprehensive, enterprise-grade health data platform! 🚀*