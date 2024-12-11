# Flux en Quarkus Project Setup Handleiding

## 1. Project Structuren

### Flux Project Structuur (`flux-minikube`)
```
flux-minikube/
└── minikube/
    └── applications/
        └── kustomize/
            └── kustomize-quarkus.yaml    # Kustomization voor je applicatie
    └── flux-system/
        └── addons.yaml                   # GitRepository definitie
```

### Quarkus Project Structuur (`kustomize-quarkus`)
```
kustomize-quarkus/
└── kubernetes/
    ├── base/
    │   ├── deployment.yaml
    │   ├── service.yaml
    │   └── kustomization.yaml
    └── overlays/
        └── development/
            └── kustomization.yaml
```

## 2. Configuratie Bestanden

### `addons.yaml` (in flux-minikube)
```yaml
apiVersion: source.toolkit.fluxcd.io/v1
kind: GitRepository
metadata:
  name: kustomize-quarkus
  namespace: flux-system
spec:
  interval: 1m
  url: https://github.com/vrecon/kustomize-quarkus
  ref:
    branch: main
  ignore: |
    /*
    !/kubernetes/
```

### `kustomize-quarkus.yaml` (in flux-minikube)
```yaml
apiVersion: kustomize.toolkit.fluxcd.io/v1beta2
kind: Kustomization
metadata:
  name: kustomize-quarkus
  namespace: flux-system
spec:
  interval: 1m0s
  prune: true
  path: ./kubernetes/overlays/development
  sourceRef:
    kind: GitRepository
    name: kustomize-quarkus
  targetNamespace: default
```

## 3. Belangrijke Commands

### Minikube en Flux Setup
```bash
# Start Minikube
minikube start

# Installeer Flux
flux install
```

### Git Repository Management
```bash
# Check repository status
flux get sources git

# Force repository synchronisatie
flux reconcile source git kustomize-quarkus
```

### Kustomization Management
```bash
# Check kustomization status
flux get kustomizations

# Force kustomization synchronisatie
flux reconcile kustomization kustomize-quarkus
```

### Applicatie Management
```bash
# Check pods
kubectl get pods

# Check services
kubectl get services

# Krijg service URL in minikube
minikube service dev-kustomize-quarkus-app --url

# Bekijk applicatie logs
kubectl logs -l app=kustomize-quarkus-app

# Bekijk deployment details
kubectl describe deployment dev-kustomize-quarkus-app
```

### Docker/Image Commands
```bash
# Build Quarkus applicatie
./mvnw package

# Build Docker image
docker build -f src/main/docker/Dockerfile.jvm -t kustomize-quarkus-app:dev .

# Load image in Minikube
minikube image load kustomize-quarkus-app:dev
```

## 4. Deployment Process
1. Maak wijzigingen in je code of Kubernetes configuratie
2. Commit en push naar GitHub
3. Flux detecteert wijzigingen automatisch
4. Check status met `flux get kustomizations`

## 5. Troubleshooting Commands
```bash
# Bekijk Flux logs
flux logs --level=debug

# Bekijk pod details
kubectl describe pod <pod-name>

# Bekijk service details
kubectl describe service dev-kustomize-quarkus-app
```

## 6. Development Workflow
1. Ontwikkel lokaal met Quarkus
2. Test lokaal met Minikube
3. Push changes naar GitHub
4. Flux synchroniseert automatisch
5. Verifieer deployment met kubectl commands

## 7. Testen van de Setup

### Test 1: Kubernetes Configuratie Wijzigingen
1. Voeg een label toe in `kubernetes/base/deployment.yaml`:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kustomize-quarkus-app
  labels:
    app: kustomize-quarkus-app
    environment: test  # Nieuwe label
spec:
  selector:
    matchLabels:
      app: kustomize-quarkus-app
  template:
    metadata:
      labels:
        app: kustomize-quarkus-app
        environment: test  # Nieuwe label
```

2. Commit en push de wijzigingen:
```bash
git add kubernetes/base/deployment.yaml
git commit -m "test: add environment label"
git push
```

3. Controleer of Flux de wijzigingen oppikt:
```bash
# Check Flux status
flux get kustomizations

# Controleer de labels
kubectl get deployment dev-kustomize-quarkus-app --show-labels
```

### Test 2: Applicatie Bereikbaarheid
1. Haal de service URL op:
```bash
minikube service dev-kustomize-quarkus-app --url
```

2. Test de endpoint (vervang URL met de output van vorig commando):
```bash
curl http://127.0.0.1:xxxxx/Hello
```

### Test 3: Pod Gezondheid
```bash
# Check pod status
kubectl get pods

# Check logs
kubectl logs -l app=kustomize-quarkus-app

# Gedetailleerde pod informatie
kubectl describe pod -l app=kustomize-quarkus-app
```

## 8. Productie Notities
Voor productie-omgevingen wordt aangeraden:
- Een CI/CD pipeline op te zetten
- Images te bouwen en pushen naar een container registry
- Gebruik te maken van image automation features van Flux
- Verschillende omgevingen (staging/productie) te configureren
- Secrets management toe te voegen
