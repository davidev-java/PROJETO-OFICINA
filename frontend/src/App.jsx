import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute } from './components/ProtectedRoute'
import { Navbar } from './components/Navbar'
import { Login } from './pages/Login'
import { LoginCallback } from './pages/LoginCallback'
import { Dashboard } from './pages/Dashboard'
import { ClientesPage } from './pages/clientes/ClientesPage'
import { ClienteForm } from './pages/clientes/ClienteForm'
import { VeiculosPage } from './pages/veiculos/VeiculosPage'
import { VeiculoForm } from './pages/veiculos/VeiculoForm'
import { OrdensPage } from './pages/ordens/OrdensPage'
import { OrdemForm } from './pages/ordens/OrdemForm'

const STAFF = ['ADMIN', 'SUPERVISOR', 'ATENDENTE', 'MECANICO']

function Layout({ children }) {
  return (
    <>
      <Navbar />
      <main>{children}</main>
    </>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/login/callback" element={<LoginCallback />} />

          <Route path="/" element={
            <ProtectedRoute>
              <Layout><Dashboard /></Layout>
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
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
