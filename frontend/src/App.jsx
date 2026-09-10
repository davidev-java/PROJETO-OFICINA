import { useEffect, useState } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './components/ProtectedRoute'
import { Sidebar } from './components/Sidebar'
import { DemoBanner } from './components/DemoBanner'
import { OverlayServidor } from './components/OverlayServidor'
import { Login } from './pages/Login'
import { LoginCallback } from './pages/LoginCallback'
import { Dashboard } from './pages/Dashboard'
import { ClientesPage } from './pages/clientes/ClientesPage'
import { ClienteForm } from './pages/clientes/ClienteForm'
import { VeiculosPage } from './pages/veiculos/VeiculosPage'
import { VeiculoForm } from './pages/veiculos/VeiculoForm'
import { OrdensPage } from './pages/ordens/OrdensPage'
import { OrdemForm } from './pages/ordens/OrdemForm'
import { MeusVeiculosPage } from './pages/cliente/MeusVeiculosPage'
import { MinhasOrdensPage } from './pages/cliente/MinhasOrdensPage'
import { UsuariosPage } from './pages/usuarios/UsuariosPage'
import { DemoPage } from './pages/demo/DemoPage'
import { DemoClientesPage } from './pages/demo/DemoClientesPage'
import { DemoVeiculosPage } from './pages/demo/DemoVeiculosPage'
import { DemoOrdensPage } from './pages/demo/DemoOrdensPage'
import { useAuth } from './context/AuthContext'
import { aquecerBackend } from './api/http'

const STAFF = ['ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO']

// Usuario do modo demo (admin_demo/visitante) nunca alcança as rotas reais
// (o backend bloqueia com 403) - a Home mostra o Painel Demo pra eles em vez
// do Dashboard normal.
function Home() {
  const { usuario } = useAuth()
  return usuario?.demo ? <DemoPage /> : <Dashboard />
}

// As telas /demo/** so fazem sentido pra quem entrou pelo modo demo; quem
// esta logado de verdade volta pro Dashboard.
function RotaDemo({ children }) {
  const { usuario } = useAuth()
  return usuario?.demo ? children : <Navigate to="/" replace />
}

function Layout({ children }) {
  const [menuAberto, setMenuAberto] = useState(false)

  return (
    <div className="app-shell">
      <Sidebar aberta={menuAberto} onFechar={() => setMenuAberto(false)} />
      <div className="conteudo">
        <div className="topo-mobile">
          <button className="sidebar-toggle" onClick={() => setMenuAberto(true)} aria-label="Abrir menu">☰</button>
          <strong>Oficina</strong>
          <span style={{ width: 34 }} />
        </div>
        <DemoBanner />
        <main className="pagina-wrap">{children}</main>
      </div>
    </div>
  )
}

export default function App() {
  // Comeca a acordar o backend assim que o site abre, antes de qualquer clique.
  useEffect(() => {
    aquecerBackend()
  }, [])

  return (
    <BrowserRouter>
      <AuthProvider>
        <OverlayServidor />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/login/callback" element={<LoginCallback />} />

          <Route path="/" element={
            <ProtectedRoute>
              <Layout><Home /></Layout>
            </ProtectedRoute>
          } />

          <Route path="/clientes" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><ClientesPage /></Layout>
            </ProtectedRoute>
          } />
          <Route path="/clientes/novo" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><ClienteForm /></Layout>
            </ProtectedRoute>
          } />
          <Route path="/clientes/:id" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><ClienteForm /></Layout>
            </ProtectedRoute>
          } />

          <Route path="/veiculos" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><VeiculosPage /></Layout>
            </ProtectedRoute>
          } />
          <Route path="/veiculos/novo" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><VeiculoForm /></Layout>
            </ProtectedRoute>
          } />
          <Route path="/veiculos/:id" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><VeiculoForm /></Layout>
            </ProtectedRoute>
          } />

          <Route path="/ordens" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><OrdensPage /></Layout>
            </ProtectedRoute>
          } />
          <Route path="/ordens/nova" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><OrdemForm /></Layout>
            </ProtectedRoute>
          } />
          <Route path="/ordens/:id" element={
            <ProtectedRoute rolesPermitidas={STAFF}>
              <Layout><OrdemForm /></Layout>
            </ProtectedRoute>
          } />

          <Route path="/meus-veiculos" element={
            <ProtectedRoute rolesPermitidas={['CLIENTE']}>
              <Layout><MeusVeiculosPage /></Layout>
            </ProtectedRoute>
          } />
          <Route path="/minhas-ordens" element={
            <ProtectedRoute rolesPermitidas={['CLIENTE']}>
              <Layout><MinhasOrdensPage /></Layout>
            </ProtectedRoute>
          } />

          <Route path="/demo/clientes" element={
            <ProtectedRoute>
              <RotaDemo><Layout><DemoClientesPage /></Layout></RotaDemo>
            </ProtectedRoute>
          } />
          <Route path="/demo/veiculos" element={
            <ProtectedRoute>
              <RotaDemo><Layout><DemoVeiculosPage /></Layout></RotaDemo>
            </ProtectedRoute>
          } />
          <Route path="/demo/ordens" element={
            <ProtectedRoute>
              <RotaDemo><Layout><DemoOrdensPage /></Layout></RotaDemo>
            </ProtectedRoute>
          } />

          <Route path="/usuarios" element={
            <ProtectedRoute rolesPermitidas={['ADMIN']}>
              <Layout><UsuariosPage /></Layout>
            </ProtectedRoute>
          } />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
