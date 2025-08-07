'use client'

import { Clock, CheckCircle, AlertCircle, Download } from 'lucide-react'

interface VideoStatusData {
  id: string;
  status: 'PROCESSING' | 'COMPLETED' | 'ERROR' | 'EM_PROCESSAMENTO' | 'CONCLUIDO';
  message?: string;
  frameCount?: number;
  zipPath?: string;
}

interface VideoStatusProps {
  videoId: string;
  status: VideoStatusData | null;
}

export default function VideoStatus({ videoId, status }: VideoStatusProps) {
  const handleDownloadZip = async () => {
    try {
      const response = await fetch(`/api/videos/download/${videoId}`)
      
      if (response.ok) {
        const blob = await response.blob()
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `frames_${videoId}.zip`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
      } else {
        throw new Error('Erro ao baixar o arquivo')
      }
    } catch (error) {
      console.error('Erro no download:', error)
      alert('Erro ao fazer download do arquivo ZIP')
    }
  }

  const getStatusIcon = () => {
    switch (status?.status) {
      case 'PROCESSING':
      case 'EM_PROCESSAMENTO':
        return <Clock className="text-blue-600 animate-pulse" size={24} />
      case 'COMPLETED':
      case 'CONCLUIDO':
        return <CheckCircle className="text-green-600" size={24} />
      case 'ERROR':
        return <AlertCircle className="text-red-600" size={24} />
      default:
        return <Clock className="text-gray-400" size={24} />
    }
  }

  const getStatusText = () => {
    switch (status?.status) {
      case 'PROCESSING':
      case 'EM_PROCESSAMENTO':
        return 'Processando vídeo...'
      case 'COMPLETED':
      case 'CONCLUIDO':
        return 'Processamento concluído!'
      case 'ERROR':
        return 'Erro no processamento'
      default:
        return 'Aguardando status...'
    }
  }

  const getStatusColor = () => {
    switch (status?.status) {
      case 'PROCESSING':
      case 'EM_PROCESSAMENTO':
        return 'bg-blue-100 border-blue-300'
      case 'COMPLETED':
      case 'CONCLUIDO':
        return 'bg-green-100 border-green-300'
      case 'ERROR':
        return 'bg-red-100 border-red-300'
      default:
        return 'bg-gray-100 border-gray-300'
    }
  }

  return (
    <div className={`bg-white rounded-lg shadow-lg p-6 border ${getStatusColor()}`}>
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          {getStatusIcon()}
          <div>
            <h3 className="text-lg font-semibold text-gray-800">
              Status do Processamento
            </h3>
            <p className="text-gray-600">{getStatusText()}</p>
            {status?.message && (
              <p className="text-sm text-gray-500 mt-1">{status.message}</p>
            )}
          </div>
        </div>

        {(status?.status === 'COMPLETED' || status?.status === 'CONCLUIDO') && (
          <button
            onClick={handleDownloadZip}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Download size={18} />
            Download ZIP
          </button>
        )}
      </div>

      {(status?.status === 'PROCESSING' || status?.status === 'EM_PROCESSAMENTO') && (
        <div className="mt-4">
          <div className="bg-blue-200 rounded-full h-2">
            <div className="bg-blue-600 h-2 rounded-full animate-pulse w-3/4"></div>
          </div>
          <p className="text-sm text-blue-600 mt-2">
            Extraindo frames do vídeo...
          </p>
        </div>
      )}

      {(status?.status === 'COMPLETED' || status?.status === 'CONCLUIDO') && status.frameCount && (
        <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-gray-600">
          <div>
            <span className="font-medium">Frames extraídos:</span> {status.frameCount}
          </div>
          <div>
            <span className="font-medium">ID do vídeo:</span> {videoId}
          </div>
        </div>
      )}

      {status?.status === 'ERROR' && (
        <div className="mt-4 p-3 bg-red-50 rounded border border-red-200">
          <p className="text-red-700 text-sm">
            {status.message || 'Ocorreu um erro durante o processamento do vídeo.'}
          </p>
        </div>
      )}
    </div>
  )
}
